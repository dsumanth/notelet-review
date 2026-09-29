package ai.nimblocity.noteletreview

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp

internal enum class MediaLoadState { Loading, Loaded, Failed }

/** 440dp matches the widest phone, so media never stretches across tablets or landscape. */
private val MAX_MEDIA_WIDTH = 440.dp
private val MIN_MEDIA_WIDTH = 300.dp
private val MEDIA_PADDING = 16.dp

@Composable
internal fun MediaNoteContent(item: NoteItem.Media, isCurrent: Boolean, accent: Color, typography: ResolvedTypography) {
    var loadState by remember(item.url) { mutableStateOf(MediaLoadState.Loading) }
    val label = stringResource(accessibilityLabel(item.kind, loadState), item.title, item.description)
    val shape = MaterialTheme.shapes.large

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .clearAndSetSemantics { contentDescription = label },
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val side = maxWidth.coerceIn(MIN_MEDIA_WIDTH, MAX_MEDIA_WIDTH) - MEDIA_PADDING * 2
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(MEDIA_PADDING)
                    .size(side)
                    .shadow(20.dp, shape)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            ) {
                when (item.kind) {
                    MediaKind.Image -> RemoteImage(item.url, accent, onLoadStateChange = { loadState = it })
                    MediaKind.Video -> LoopingVideo(item.url, isPlaying = isCurrent, accent, onLoadStateChange = { loadState = it })
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 30.dp),
        ) {
            Text(item.title, style = typography.mediaTitle, color = MaterialTheme.colorScheme.onSurface)
            Text(item.description, style = typography.body, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@StringRes
private fun accessibilityLabel(kind: MediaKind, state: MediaLoadState): Int = when (kind) {
    MediaKind.Image -> when (state) {
        MediaLoadState.Loading -> R.string.notelet_image_loading
        MediaLoadState.Loaded -> R.string.notelet_image_loaded
        MediaLoadState.Failed -> R.string.notelet_image_failed
    }
    MediaKind.Video -> when (state) {
        MediaLoadState.Loading -> R.string.notelet_video_loading
        MediaLoadState.Loaded -> R.string.notelet_video_loaded
        MediaLoadState.Failed -> R.string.notelet_video_failed
    }
}
