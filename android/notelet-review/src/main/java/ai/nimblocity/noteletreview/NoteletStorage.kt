package ai.nimblocity.noteletreview

import android.content.Context
import android.content.SharedPreferences

/** The "latest seen version" used by [PresentedVersion.Current]. */
object NoteletStorage {
    private const val PREFS_NAME = "notelet"
    private const val KEY_LATEST_SEEN_APP_VERSION = "Notelet.LatestSeenAppVersion"

    /**
     * Mark the installed version as seen. Call this when onboarding finishes
     * so new users don't get release notes (or a review request) right away.
     */
    fun markCurrentVersionAsSeen(context: Context) {
        preferences(context).edit()
            .putString(KEY_LATEST_SEEN_APP_VERSION, context.currentAppVersion())
            .apply()
    }

    /** Clear the seen version so the next [PresentedVersion.Current] shows again. For debugging. */
    fun resetSeenVersion(context: Context) {
        preferences(context).edit().remove(KEY_LATEST_SEEN_APP_VERSION).apply()
    }

    fun getLatestSeenAppVersion(context: Context): String? =
        preferences(context).getString(KEY_LATEST_SEEN_APP_VERSION, null)

    internal fun preferences(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}

internal fun Context.currentAppVersion(): String =
    runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull() ?: "0"
