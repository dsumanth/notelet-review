package ai.nimblocity.noteletreview

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NoteletThemeTest {
    private val appTypography = Typography(
        headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 30.sp),
        titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 22.sp),
        titleMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 16.sp),
        bodyLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 16.sp),
    )

    @Test fun inheritsTheHostAppAccentByDefault() = assertNull(NoteletConfiguration().accentColor)

    @Test fun derivesFontsFromTheHostAppTypography() {
        val resolved = NoteletTypography().resolve(appTypography)
        assertEquals(appTypography.headlineMedium.copy(fontWeight = FontWeight.Bold), resolved.pageTitle)
        assertEquals(appTypography.titleLarge.copy(fontWeight = FontWeight.Bold), resolved.mediaTitle)
        assertEquals(appTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold), resolved.rowTitle)
        assertEquals(appTypography.bodyLarge, resolved.body)
    }

    @Test fun keepsExplicitOverrides() {
        val custom = TextStyle(fontFamily = FontFamily.Cursive, fontSize = 40.sp)
        assertEquals(custom, NoteletTypography(pageTitle = custom).resolve(appTypography).pageTitle)
    }
}
