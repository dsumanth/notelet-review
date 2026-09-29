package ai.nimblocity.noteletreview

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReviewPromptStoreTest {
    private val prefs = ApplicationProvider.getApplicationContext<Context>()
        .getSharedPreferences("ReviewPromptStoreTest", Context.MODE_PRIVATE)
        .also { it.edit().clear().commit() }

    @Test fun startsEmpty() {
        val store = ReviewPromptStore(prefs)
        assertNull(store.lastPromptedVersion)
        assertNull(store.lastPromptAtMillis)
    }

    @Test fun recordsThePrompt() {
        ReviewPromptStore(prefs).recordPrompt(version = "2.0", atMillis = 1_800_000_000_000L)

        val reloaded = ReviewPromptStore(prefs)
        assertEquals("2.0", reloaded.lastPromptedVersion)
        assertEquals(1_800_000_000_000L, reloaded.lastPromptAtMillis)
    }
}
