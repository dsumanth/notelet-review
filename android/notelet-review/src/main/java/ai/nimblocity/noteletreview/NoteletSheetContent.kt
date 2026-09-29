package ai.nimblocity.noteletreview

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NoteletSheetContent(
    items: List<NoteItem>,
    configuration: NoteletConfiguration,
    onDismissed: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState = rememberPagerState { items.size }
    val scope = rememberCoroutineScope()
    val accent = configuration.accentColor ?: MaterialTheme.colorScheme.primary
    val onAccent = when {
        configuration.accentColor == null -> MaterialTheme.colorScheme.onPrimary
        accent.luminance() > 0.5f -> Color.Black
        else -> Color.White
    }
    val typography = configuration.typography.resolve(MaterialTheme.typography)
    val isOnLastPage = pagerState.currentPage >= items.lastIndex

    ModalBottomSheet(onDismissRequest = onDismissed, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(configuration.sheetHeight.fraction),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top,
            ) { page ->
                when (val item = items[page]) {
                    is NoteItem.List -> ListNoteContent(item, accent, typography)
                    is NoteItem.Media -> MediaNoteContent(item, isCurrent = page == pagerState.currentPage, accent, typography)
                }
            }

            if (items.size > 1) {
                PageIndicator(
                    pageCount = items.size,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier.padding(top = 14.dp),
                )
            }

            Button(
                onClick = {
                    if (isOnLastPage) {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissed() }
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = onAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 16.dp),
            ) {
                Text(
                    text = if (isOnLastPage) {
                        configuration.doneButtonLabel ?: stringResource(R.string.notelet_done)
                    } else {
                        configuration.nextButtonLabel ?: stringResource(R.string.notelet_next)
                    },
                    style = typography.rowTitle,
                    modifier = Modifier.padding(vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    val label = stringResource(R.string.notelet_page_indicator, currentPage + 1, pageCount)
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier.clearAndSetSemantics { contentDescription = label },
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            val width by animateDpAsState(if (isSelected) 14.dp else 7.dp, label = "notelet-indicator")
            val color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
            Box(
                Modifier
                    .size(width = width, height = 7.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.35f)),
            )
        }
    }
}
