package ai.nimblocity.noteletreview

/** Decides which notes, if any, the sheet should show. */
internal object SheetPresentation {
    fun itemsToPresent(
        notes: List<VersionNotes>,
        version: PresentedVersion?,
        currentVersion: String,
        latestSeenVersion: String?,
    ): List<NoteItem> {
        val versionToShow = when (version) {
            PresentedVersion.Current -> {
                if (latestSeenVersion == currentVersion) return emptyList()
                currentVersion
            }
            is PresentedVersion.Specific -> version.version
            null -> return emptyList()
        }
        return notes.firstOrNull { it.version == versionToShow }?.items.orEmpty()
    }
}
