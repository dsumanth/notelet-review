package ai.nimblocity.noteletreview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Google's and Apple's guidance: never let the prompt land on top of the dismissal. */
private const val PROMPT_DELAY_MILLIS = 2_000L

/**
 * A release notes sheet that asks for a Play rating right after the user
 * closes it.
 *
 * The review request only fires when the sheet was shown automatically for
 * an app update ([PresentedVersion.Current]), at most once per version and
 * at least two weeks apart. See [ReviewPromptPolicy] for the full rule set.
 *
 * [canRequestReview] lets the app skip the request (for example when it asked
 * recently from another screen) and [onReviewRequested] lets it count the
 * request against its own review budget.
 *
 * Styling comes from the host app's `MaterialTheme` unless overridden in
 * [configuration].
 */
@Composable
fun NoteletReviewSheet(
    notes: List<VersionNotes>,
    version: PresentedVersion?,
    onDismiss: () -> Unit = {},
    configuration: NoteletConfiguration = NoteletConfiguration(),
    canRequestReview: () -> Boolean = { true },
    onReviewRequested: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Read before the sheet marks the current version as seen, so a fresh
    // install (nothing seen yet) can be told apart from an update.
    var previouslySeenVersion by remember { mutableStateOf(NoteletStorage.getLatestSeenAppVersion(context)) }

    NoteletSheet(
        notes = notes,
        version = version,
        configuration = configuration,
        onDismiss = {
            onDismiss()

            val currentVersion = context.currentAppVersion()
            val store = ReviewPromptStore(NoteletStorage.preferences(context))
            val now = System.currentTimeMillis()
            val shouldAsk = ReviewPromptPolicy.shouldRequestReview(
                wasAutoPresented = version == PresentedVersion.Current,
                hostAllowsReview = canRequestReview(),
                previouslySeenVersion = previouslySeenVersion,
                currentVersion = currentVersion,
                lastPromptedVersion = store.lastPromptedVersion,
                lastPromptAtMillis = store.lastPromptAtMillis,
                nowMillis = now,
            )
            previouslySeenVersion = currentVersion
            if (shouldAsk) {
                store.recordPrompt(currentVersion, now)
                onReviewRequested()
                scope.launch {
                    delay(PROMPT_DELAY_MILLIS)
                    PlayReviewRequester.request(context)
                }
            }
        },
    )
}
