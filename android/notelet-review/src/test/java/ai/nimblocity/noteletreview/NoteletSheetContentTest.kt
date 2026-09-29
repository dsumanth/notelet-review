package ai.nimblocity.noteletreview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NoteletSheetContentTest {
    @get:Rule val compose = createComposeRule()

    private val items = listOf(
        NoteItem.List("What's new", listOf(ListRow(Icons.Filled.Star, "Faster sync", "Moods arrive instantly."))),
        NoteItem.List("Also new", listOf(ListRow(Icons.Filled.Star, "Widgets", "See your partner at a glance."))),
    )

    @Test fun pagesThroughNotesAndDismissesOnDone() {
        var dismissed = false
        compose.setContent {
            MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFFE91E63))) {
                NoteletSheetContent(items, NoteletConfiguration(), onDismissed = { dismissed = true })
            }
        }

        compose.onNodeWithText("What's new").assertIsDisplayed()
        compose.onNodeWithText("Next").performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Done").performClick()
        compose.waitForIdle()

        assertTrue(dismissed)
    }

    @Test fun usesTheAppsOwnButtonLabels() {
        compose.setContent {
            MaterialTheme {
                NoteletSheetContent(items.take(1), NoteletConfiguration(doneButtonLabel = "Got it"), onDismissed = {})
            }
        }

        compose.onNodeWithText("Got it").assertIsDisplayed()
    }
}
