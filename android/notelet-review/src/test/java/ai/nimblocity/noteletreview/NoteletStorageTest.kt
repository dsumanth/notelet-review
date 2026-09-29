package ai.nimblocity.noteletreview

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NoteletStorageTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test fun marksAnArbitraryVersionAsSeen() {
        // Apps adopting the library mark existing users as updaters.
        NoteletStorage.markVersionAsSeen(context, "legacy")

        assertEquals("legacy", NoteletStorage.getLatestSeenAppVersion(context))
    }
}
