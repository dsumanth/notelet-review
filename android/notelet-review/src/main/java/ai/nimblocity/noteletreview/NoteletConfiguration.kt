package ai.nimblocity.noteletreview

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Optional overrides. Anything left `null` comes from the host app's
 * `MaterialTheme`, so the sheet matches the app's design language by default.
 */
data class NoteletConfiguration(
    /** Defaults to a localized "Next". */
    val nextButtonLabel: String? = null,
    /** Defaults to a localized "Done". */
    val doneButtonLabel: String? = null,
    /** Defaults to `MaterialTheme.colorScheme.primary`. */
    val accentColor: Color? = null,
    val typography: NoteletTypography = NoteletTypography(),
    val sheetHeight: SheetHeight = SheetHeight.Standard,
)

/** Text style overrides. Each `null` style is derived from the app's `MaterialTheme.typography`. */
data class NoteletTypography(
    val pageTitle: TextStyle? = null,
    val mediaTitle: TextStyle? = null,
    val rowTitle: TextStyle? = null,
    val body: TextStyle? = null,
) {
    internal fun resolve(appTypography: Typography) = ResolvedTypography(
        pageTitle = pageTitle ?: appTypography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        mediaTitle = mediaTitle ?: appTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
        rowTitle = rowTitle ?: appTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        body = body ?: appTypography.bodyLarge,
    )
}

internal data class ResolvedTypography(
    val pageTitle: TextStyle,
    val mediaTitle: TextStyle,
    val rowTitle: TextStyle,
    val body: TextStyle,
)
