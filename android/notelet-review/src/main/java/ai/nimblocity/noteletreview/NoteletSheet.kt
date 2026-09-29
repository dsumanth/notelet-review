package ai.nimblocity.noteletreview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

/** Shows the notes for [version] and records the current version as seen on dismiss. */
@Composable
internal fun NoteletSheet(
    notes: List<VersionNotes>,
    version: PresentedVersion?,
    configuration: NoteletConfiguration,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var items by remember { mutableStateOf(emptyList<NoteItem>()) }

    LaunchedEffect(version) {
        items = SheetPresentation.itemsToPresent(
            notes = notes,
            version = version,
            currentVersion = context.currentAppVersion(),
            latestSeenVersion = NoteletStorage.getLatestSeenAppVersion(context),
        )
    }

    if (items.isNotEmpty()) {
        NoteletSheetContent(
            items = items,
            configuration = configuration,
            onDismissed = {
                items = emptyList()
                if (version == PresentedVersion.Current) NoteletStorage.markCurrentVersionAsSeen(context)
                onDismiss()
            },
        )
    }
}
