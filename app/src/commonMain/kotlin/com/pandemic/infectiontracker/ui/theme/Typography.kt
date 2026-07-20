package com.pandemic.infectiontracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import com.pandemic.infectiontracker.generated.resources.Res
import com.pandemic.infectiontracker.generated.resources.gunplay
import com.pandemic.infectiontracker.generated.resources.helvetica_neue_condensed_bold

@Composable
fun getPandemicFontFamily() = FontFamily(
    Font(Res.font.helvetica_neue_condensed_bold, FontWeight.Bold)
)

@Composable
fun getButtonFontFamily() = FontFamily(
    Font(Res.font.gunplay, FontWeight.Bold)
)

@Composable
fun getPandemicTypography(): Typography {
    val pandemicFont = getPandemicFontFamily()
    val buttonFont = getButtonFontFamily()
    return Typography(
        headlineSmall = TextStyle(
            fontFamily = pandemicFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = pandemicFont,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = pandemicFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.25.sp
        ),
        labelLarge = TextStyle(
            fontFamily = buttonFont,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            letterSpacing = 0.sp
        ),
        labelMedium = TextStyle(
            fontFamily = buttonFont,
            fontSize = 16.sp,
            letterSpacing = 0.25.sp
        ),
    )
}
