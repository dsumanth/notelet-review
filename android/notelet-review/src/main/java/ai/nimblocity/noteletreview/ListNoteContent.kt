package ai.nimblocity.noteletreview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun ListNoteContent(item: NoteItem.List, accent: Color, typography: ResolvedTypography) {
    Column(
        verticalArrangement = Arrangement.spacedBy(40.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 40.dp, vertical = 48.dp),
    ) {
        Text(item.title, style = typography.pageTitle, color = MaterialTheme.colorScheme.onSurface)

        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            item.rows.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    modifier = Modifier.semantics(mergeDescendants = true) {},
                ) {
                    Box(Modifier.width(48.dp), contentAlignment = Alignment.Center) {
                        Icon(row.icon, contentDescription = null, tint = accent, modifier = Modifier.size(32.dp))
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(row.title, style = typography.rowTitle, color = MaterialTheme.colorScheme.onSurface)
                        Text(row.description, style = typography.body, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
