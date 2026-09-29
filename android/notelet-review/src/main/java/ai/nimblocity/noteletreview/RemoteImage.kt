package ai.nimblocity.noteletreview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@Composable
internal fun RemoteImage(url: String, accent: Color, onLoadStateChange: (MediaLoadState) -> Unit) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        loading = { CircularProgressIndicator(color = accent, modifier = Modifier.padding(120.dp)) },
        error = {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(80.dp),
            )
        },
        onLoading = { onLoadStateChange(MediaLoadState.Loading) },
        onSuccess = { onLoadStateChange(MediaLoadState.Loaded) },
        onError = { onLoadStateChange(MediaLoadState.Failed) },
    )
}
