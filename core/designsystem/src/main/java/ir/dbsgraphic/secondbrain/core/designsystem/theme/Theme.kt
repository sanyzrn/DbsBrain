package ir.dbsgraphic.secondbrain.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalSbColors = staticCompositionLocalOf<SbColors> {
    error("SbColors not provided — wrap content in SecondBrainTheme")
}
private val LocalSbTypography = staticCompositionLocalOf { SbTypography() }
private val LocalSbSpacing = staticCompositionLocalOf { SbSpacing() }
private val LocalSbShapes = staticCompositionLocalOf { SbShapes() }

/**
 * Root theme. Pine Editorial is light-primary; dark is offered for low-light
 * capture but the system default is light unless the device asks otherwise.
 *
 * We intentionally do NOT lean on Material's color/typography theming — the
 * look is our own, built on Compose Foundation (design spine).
 */
@Composable
fun SecondBrainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) PineEditorialDark else PineEditorialLight
    CompositionLocalProvider(
        LocalSbColors provides colors,
        LocalSbTypography provides SbTypography(),
        LocalSbSpacing provides SbSpacing(),
        LocalSbShapes provides SbShapes(),
        content = content,
    )
}

/** Ergonomic accessor: `SecondBrainTheme.colors`, `.type`, `.spacing`, `.shapes`. */
object SecondBrainTheme {
    val colors: SbColors
        @Composable @ReadOnlyComposable get() = LocalSbColors.current
    val type: SbTypography
        @Composable @ReadOnlyComposable get() = LocalSbTypography.current
    val spacing: SbSpacing
        @Composable @ReadOnlyComposable get() = LocalSbSpacing.current
    val shapes: SbShapes
        @Composable @ReadOnlyComposable get() = LocalSbShapes.current
}
