package ai.nimblocity.noteletreview

/**
 * Decides whether dismissing the release notes sheet should be followed by
 * the Play in-app review flow. The rules follow Apple's review guidance,
 * applied on Android too, and are compatible with Google Play's policy:
 *
 * - Only the platform API asks for a rating (App Review Guideline 5.6.1;
 *   Play: don't ask "Do you like the app?" before the flow). There is no
 *   custom pre-prompt anywhere in this library.
 * - Don't ask on first launch or during onboarding (HIG).
 * - Don't ask in response to a user action such as a "What's new" button
 *   (HIG; Play: no call-to-action that triggers the flow).
 * - Avoid pestering: at least two weeks between requests (HIG).
 * - At most once per app version (Apple's "Requesting App Store reviews" sample).
 *
 * Play also enforces its own quota and may show nothing, which is why this
 * is a request, never a guarantee.
 */
internal object ReviewPromptPolicy {
    const val MINIMUM_INTERVAL_MILLIS = 14L * 24 * 60 * 60 * 1000

    fun shouldRequestReview(
        wasAutoPresented: Boolean,
        previouslySeenVersion: String?,
        currentVersion: String,
        lastPromptedVersion: String?,
        lastPromptAtMillis: Long?,
        nowMillis: Long,
    ): Boolean {
        if (!wasAutoPresented) return false
        if (previouslySeenVersion == null || previouslySeenVersion == currentVersion) return false
        if (lastPromptedVersion == currentVersion) return false
        if (lastPromptAtMillis == null) return true
        return nowMillis - lastPromptAtMillis >= MINIMUM_INTERVAL_MILLIS
    }
}
