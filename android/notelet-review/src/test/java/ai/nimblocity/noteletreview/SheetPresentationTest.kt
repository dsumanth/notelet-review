package ai.nimblocity.noteletreview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SheetPresentationTest {
    private val notes = listOf(
        VersionNotes("1.1", listOf(NoteItem.Media(MediaKind.Image, "https://example.com/a.jpg", "A", "a"))),
        VersionNotes("1.0", listOf(NoteItem.Media(MediaKind.Image, "https://example.com/b.jpg", "B", "b"))),
    )

    private fun itemsFor(version: PresentedVersion?, seen: String? = null) =
        SheetPresentation.itemsToPresent(notes, version, currentVersion = "1.1", latestSeenVersion = seen)

    @Test fun showsCurrentVersionNotesWhenUnseen() = assertEquals(notes[0].items, itemsFor(PresentedVersion.Current))

    @Test fun skipsCurrentVersionOnceSeen() = assertTrue(itemsFor(PresentedVersion.Current, seen = "1.1").isEmpty())

    @Test fun showsASpecificVersionEvenIfSeen() =
        assertEquals(notes[1].items, itemsFor(PresentedVersion.Specific("1.0"), seen = "1.1"))

    @Test fun showsNothingWithoutAVersion() = assertTrue(itemsFor(null).isEmpty())

    @Test fun showsNothingForAVersionWithoutNotes() = assertTrue(itemsFor(PresentedVersion.Specific("9.9")).isEmpty())
}
