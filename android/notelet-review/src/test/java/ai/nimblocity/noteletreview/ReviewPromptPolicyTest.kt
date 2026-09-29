package ai.nimblocity.noteletreview

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReviewPromptPolicyTest {
    private val now = 1_800_000_000_000L
    private val day = 24L * 60 * 60 * 1000

    private fun decide(
        wasAutoPresented: Boolean = true,
        previouslySeenVersion: String? = "1.0",
        currentVersion: String = "1.1",
        lastPromptedVersion: String? = null,
        lastPromptAtMillis: Long? = null,
        hostAllowsReview: Boolean = true,
    ) = ReviewPromptPolicy.shouldRequestReview(
        wasAutoPresented = wasAutoPresented,
        hostAllowsReview = hostAllowsReview,
        previouslySeenVersion = previouslySeenVersion,
        currentVersion = currentVersion,
        lastPromptedVersion = lastPromptedVersion,
        lastPromptAtMillis = lastPromptAtMillis,
        nowMillis = now,
    )

    @Test fun asksAfterAutoShownNotesForAnUpdatingUser() = assertTrue(decide())

    @Test fun neverAsksAfterNotesOpenedManually() = assertFalse(decide(wasAutoPresented = false))

    @Test fun neverAsksWhenTheHostAppVetoes() = assertFalse(decide(hostAllowsReview = false))

    @Test fun neverAsksOnFreshInstall() = assertFalse(decide(previouslySeenVersion = null))

    @Test fun asksAtMostOncePerVersion() =
        assertFalse(decide(lastPromptedVersion = "1.1", lastPromptAtMillis = now - 90 * day))

    @Test fun waitsAtLeastTwoWeeksBetweenAsks() {
        assertFalse(decide(lastPromptedVersion = "1.0", lastPromptAtMillis = now - 13 * day))
        assertTrue(decide(lastPromptedVersion = "1.0", lastPromptAtMillis = now - 14 * day))
    }
}
