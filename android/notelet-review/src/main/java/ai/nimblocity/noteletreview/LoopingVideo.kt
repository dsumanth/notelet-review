package ai.nimblocity.noteletreview

import android.graphics.Color as AndroidColor
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

/** A looping, aspect-filled video that only plays while its page is current and the app is resumed. */
@OptIn(UnstableApi::class)
@Composable
internal fun LoopingVideo(url: String, isPlaying: Boolean, accent: Color, onLoadStateChange: (MediaLoadState) -> Unit) {
    val context = LocalContext.current
    val currentOnLoadStateChange by rememberUpdatedState(onLoadStateChange)
    var isLoading by remember(url) { mutableStateOf(true) }
    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
        }
    }

    DisposableEffect(player) {
        currentOnLoadStateChange(MediaLoadState.Loading)
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY && isLoading) {
                    isLoading = false
                    currentOnLoadStateChange(MediaLoadState.Loaded)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isLoading = false
                currentOnLoadStateChange(MediaLoadState.Failed)
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LifecycleResumeEffect(player, isPlaying) {
        player.playWhenReady = isPlaying
        onPauseOrDispose { player.playWhenReady = false }
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    setShutterBackgroundColor(AndroidColor.TRANSPARENT)
                }
            },
            update = { it.player = player },
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isLoading) 0f else 1f),
        )
        if (isLoading) CircularProgressIndicator(color = accent)
    }
}
