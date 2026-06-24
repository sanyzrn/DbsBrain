package ir.dbsgraphic.secondbrain.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import ir.dbsgraphic.secondbrain.core.designsystem.theme.SecondBrainTheme

/**
 * The one text-input primitive, built on Foundation's BasicTextField so the
 * look is entirely ours (no Material text field). The caret uses the accent —
 * one of the few places Deep Pine is allowed to appear.
 *
 * Container styling (background, radius) is the caller's job, so the same field
 * serves the quick-add bar, search, and editors.
 */
@Composable
fun SbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textStyle: TextStyle = SecondBrainTheme.type.body,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
) {
    val colors = SecondBrainTheme.colors
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle.merge(TextStyle(color = colors.text)),
        cursorBrush = SolidColor(colors.accent),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    SbText(
                        text = placeholder,
                        style = textStyle,
                        color = colors.muted,
                    )
                }
                innerTextField()
            }
        },
    )
}
