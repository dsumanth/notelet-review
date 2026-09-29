package ai.nimblocity.noteletreview

import android.content.SharedPreferences

/**
 * Persists when the review request was last made, so [ReviewPromptPolicy]
 * can enforce once-per-version and the minimum interval.
 */
internal class ReviewPromptStore(private val prefs: SharedPreferences) {
    val lastPromptedVersion: String?
        get() = prefs.getString(KEY_LAST_PROMPTED_VERSION, null)

    val lastPromptAtMillis: Long?
        get() = if (prefs.contains(KEY_LAST_PROMPT_AT)) prefs.getLong(KEY_LAST_PROMPT_AT, 0L) else null

    fun recordPrompt(version: String, atMillis: Long) {
        prefs.edit()
            .putString(KEY_LAST_PROMPTED_VERSION, version)
            .putLong(KEY_LAST_PROMPT_AT, atMillis)
            .apply()
    }

    private companion object {
        const val KEY_LAST_PROMPTED_VERSION = "NoteletReview.LastPromptedVersion"
        const val KEY_LAST_PROMPT_AT = "NoteletReview.LastPromptAt"
    }
}
