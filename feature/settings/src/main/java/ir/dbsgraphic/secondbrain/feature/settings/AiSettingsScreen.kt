package ir.dbsgraphic.secondbrain.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbCard
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbPrimaryButton
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbSwitch
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbText
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbTextButton
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbTextField
import ir.dbsgraphic.secondbrain.core.designsystem.theme.SecondBrainTheme

@Composable
fun AiSettingsRoute(
    onBack: () -> Unit,
    viewModel: AiSettingsViewModel = hiltViewModel(),
) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    AiSettingsScreen(
        enabled = config.enabled,
        baseUrl = config.baseUrl,
        apiKey = config.apiKey,
        chatModel = config.chatModel,
        transcribeModel = config.transcribeModel,
        onEnabledChange = viewModel::setEnabled,
        onSave = viewModel::save,
        onBack = onBack,
    )
}

@Composable
fun AiSettingsScreen(
    enabled: Boolean,
    baseUrl: String,
    apiKey: String,
    chatModel: String,
    transcribeModel: String,
    onEnabledChange: (Boolean) -> Unit,
    onSave: (String, String, String, String) -> Unit,
    onBack: () -> Unit,
) {
    val colors = SecondBrainTheme.colors
    val type = SecondBrainTheme.type
    val space = SecondBrainTheme.spacing

    var url by remember(baseUrl) { mutableStateOf(baseUrl) }
    var key by remember(apiKey) { mutableStateOf(apiKey) }
    var chat by remember(chatModel) { mutableStateOf(chatModel) }
    var transcribe by remember(transcribeModel) { mutableStateOf(transcribeModel) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = space.xl)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(Modifier.height(space.lg))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SbText(text = "دستیار هوشمند", style = type.title)
            SbTextButton(label = "بازگشت", onClick = onBack)
        }

        Spacer(Modifier.height(space.lg))
        SbText(
            text = "هوش مصنوعی فقط پیشنهاد می‌دهد؛ تصمیم همیشه با توست. پیش‌فرض خاموش است و " +
                "برنامه بدون آن کامل کار می‌کند.",
            style = type.body,
            color = colors.muted,
        )
        Spacer(Modifier.height(space.lg))

        SbCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SbText(text = "روشن باشد", style = type.bodyLarge)
                SbSwitch(checked = enabled, onCheckedChange = onEnabledChange)
            }
        }

        Spacer(Modifier.height(space.lg))
        LabeledField("آدرس سرویس (Base URL)", url, KeyboardType.Uri) { url = it }
        Spacer(Modifier.height(space.md))
        LabeledField("کلید API", key, KeyboardType.Password) { key = it }
        Spacer(Modifier.height(space.md))
        LabeledField("مدل گفتگو", chat, KeyboardType.Text) { chat = it }
        Spacer(Modifier.height(space.md))
        LabeledField("مدل رونویسی صدا", transcribe, KeyboardType.Text) { transcribe = it }

        Spacer(Modifier.height(space.xl))
        SbPrimaryButton(
            label = "ذخیره",
            onClick = { onSave(url, key, chat, transcribe) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(space.md))
        SbText(
            text = "کلید روی همین دستگاه ذخیره می‌شود و جای دیگری نمی‌رود.",
            style = type.monoSmall,
            color = colors.muted,
        )
        Spacer(Modifier.height(space.xxl))
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    keyboardType: KeyboardType,
    onValueChange: (String) -> Unit,
) {
    val colors = SecondBrainTheme.colors
    val space = SecondBrainTheme.spacing
    Column {
        SbText(text = label, style = SecondBrainTheme.type.monoSmall, color = colors.muted)
        Spacer(Modifier.height(space.xs))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SecondBrainTheme.shapes.medium)
                .background(colors.surface)
                .padding(horizontal = space.md, vertical = space.md),
        ) {
            SbTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = "",
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
            )
        }
    }
}
