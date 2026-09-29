package ai.nimblocity.noteletreview

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.ktx.launchReview
import com.google.android.play.core.ktx.requestReview
import kotlinx.coroutines.CancellationException

/** Launches Google Play's in-app review flow. Failures are silent by design. */
internal object PlayReviewRequester {
    suspend fun request(context: Context) {
        val activity = context.findActivity() ?: return
        val manager = ReviewManagerFactory.create(activity)
        try {
            manager.launchReview(activity, manager.requestReview())
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // No Play Store, quota reached, or offline: nothing to tell the user.
        }
    }

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
