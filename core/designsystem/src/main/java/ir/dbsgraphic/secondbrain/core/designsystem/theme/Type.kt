package ir.dbsgraphic.secondbrain.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import ir.dbsgraphic.secondbrain.core.designsystem.R

// ─── Type voices ─────────────────────────────────────────────────────────
// Yekan Bakh is the human voice (Persian first). Space Mono is the instrument
// voice — used ONLY for data: timestamps, counts, IDs, the command bar.

val YekanBakh = FontFamily(
    Font(R.font.yekan_bakh_regular, FontWeight.Normal),
    Font(R.font.yekan_bakh_bold, FontWeight.Bold),
)

val SpaceMono = FontFamily(
    Font(R.font.space_mono_regular, FontWeight.Normal),
    Font(R.font.space_mono_bold, FontWeight.Bold),
)

// Persian needs generous line-height (~1.7–1.8) and NO Latin-style tracking.
private val persianLineHeight = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun persian(
    size: Int,
    lineHeightRatio: Float,
    weight: FontWeight = FontWeight.Normal,
) = TextStyle(
    fontFamily = YekanBakh,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * lineHeightRatio).sp,
    letterSpacing = 0.em, // never track Persian
    lineHeightStyle = persianLineHeight,
)

private fun mono(size: Int, weight: FontWeight = FontWeight.Normal) = TextStyle(
    fontFamily = SpaceMono,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * 1.4f).sp,
    letterSpacing = 0.em,
)

/**
 * The type scale. Display/title/body are Yekan Bakh with a real Persian
 * rhythm; mono roles are Space Mono for data only.
 */
@Immutable
data class SbTypography(
    val display: TextStyle = persian(34, 1.55f, FontWeight.Bold),
    val title: TextStyle = persian(22, 1.6f, FontWeight.Bold),
    val bodyLarge: TextStyle = persian(18, 1.75f),
    val body: TextStyle = persian(16, 1.8f),
    val label: TextStyle = persian(14, 1.7f),
    val caption: TextStyle = persian(13, 1.7f, FontWeight.Bold),
    val mono: TextStyle = mono(14),
    val monoSmall: TextStyle = mono(12),
)
