package ir.dbsgraphic.secondbrain.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import ir.dbsgraphic.secondbrain.core.designsystem.theme.SecondBrainTheme

/**
 * A calm grouped container — tonal surface, deliberate radius, no shadow.
 * The building block for settings-style grouped rows.
 */
@Composable
fun SbCard(
    modifier: Modifier = Modifier,
    padding: Dp = SecondBrainTheme.spacing.lg,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SecondBrainTheme.shapes.large)
            .background(SecondBrainTheme.colors.surface)
            .padding(padding),
        content = content,
    )
}
