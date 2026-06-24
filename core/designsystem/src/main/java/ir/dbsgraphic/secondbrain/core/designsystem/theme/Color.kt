package ir.dbsgraphic.secondbrain.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// ─── Pine Editorial palette ──────────────────────────────────────────────
// Light is primary (warm paper). Dark is available for low-light capture.
// The accent is Deep Pine — it reads as ink, never as a neon signal, and is
// spent in exactly one place: the Timeline spine and the single primary action.

// Light — warm paper
internal val Paper = Color(0xFFF3EEE6)
internal val PaperRaised = Color(0xFFFBF8F2)
internal val InkLight = Color(0xFF1C1815)
internal val MutedLight = Color(0xFF7A7066)
internal val HairlineLight = Color(0xFFE4DCCF)

// Dark — warm ink (never pure black)
internal val Ink = Color(0xFF16120E)
internal val SurfaceDark = Color(0xFF211B15)
internal val TextDark = Color(0xFFEDE6DB)
internal val MutedDark = Color(0xFF8C8276)
internal val HairlineDark = Color(0xFF2E2820)

// Signature accent — Deep Pine
internal val Pine = Color(0xFF1F6F5C)
internal val PineDim = Color(0xFF184F43)
internal val OnPine = Color(0xFFF3EEE6)

/**
 * The semantic color set the UI reads from. Components never reference raw
 * hex values — only these roles — so a second palette can be swapped in
 * without touching feature code (Constitution §16).
 */
@Immutable
data class SbColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val muted: Color,
    val hairline: Color,
    val accent: Color,
    val accentDim: Color,
    val onAccent: Color,
    val isDark: Boolean,
)

val PineEditorialLight = SbColors(
    background = Paper,
    surface = PaperRaised,
    text = InkLight,
    muted = MutedLight,
    hairline = HairlineLight,
    accent = Pine,
    accentDim = PineDim,
    onAccent = OnPine,
    isDark = false,
)

val PineEditorialDark = SbColors(
    background = Ink,
    surface = SurfaceDark,
    text = TextDark,
    muted = MutedDark,
    hairline = HairlineDark,
    accent = Pine,
    accentDim = PineDim,
    onAccent = OnPine,
    isDark = true,
)
