package ir.dbsgraphic.secondbrain.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** 4px base grid. Generous negative space is part of the calm. */
@Immutable
data class SbSpacing(
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 12.dp,
    val lg: Dp = 16.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val xxxl: Dp = 48.dp,
)

/** Deliberate radius — not 0 (broadsheet cliché), not pill-everything. */
@Immutable
data class SbShapes(
    val small: Shape = RoundedCornerShape(8.dp),
    val medium: Shape = RoundedCornerShape(14.dp),
    val large: Shape = RoundedCornerShape(22.dp),
)

/** Hairline weight for editorial rules. */
val HairlineWidth: Dp = 1.dp
