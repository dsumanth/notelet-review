package ai.nimblocity.noteletreview

import androidx.compose.ui.graphics.vector.ImageVector

/** Release notes for one app version, matched against `versionName`. */
data class VersionNotes(
    val version: String,
    val items: List<NoteItem>,
)

/** One page of the release notes sheet. */
sealed interface NoteItem {
    /** A title and rows of icon + title + description. Ideal as a quick summary. */
    data class List(
        val title: String,
        val rows: kotlin.collections.List<ListRow>,
    ) : NoteItem

    /** A square image or looping video loaded from [url], with a title and description. */
    data class Media(
        val kind: MediaKind,
        val url: String,
        val title: String,
        val description: String,
    ) : NoteItem
}

data class ListRow(
    val icon: ImageVector,
    val title: String,
    val description: String,
)

enum class MediaKind { Image, Video }

sealed interface PresentedVersion {
    /** The installed app version. Shown once, then marked as seen on dismiss. */
    data object Current : PresentedVersion

    /** A specific version, for example from a changelog entry in settings. */
    data class Specific(val version: String) : PresentedVersion
}

/** How tall the sheet is. */
enum class SheetHeight(internal val fraction: Float) {
    /** 85% of the screen, leaving a sliver of the presenting screen visible. */
    Standard(0.85f),

    /** The full height. */
    Full(1f),
}
