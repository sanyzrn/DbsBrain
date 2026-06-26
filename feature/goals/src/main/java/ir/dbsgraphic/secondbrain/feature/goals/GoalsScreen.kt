package ir.dbsgraphic.secondbrain.feature.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbHairline
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbText
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbTextField
import ir.dbsgraphic.secondbrain.core.designsystem.theme.SecondBrainTheme
import ir.dbsgraphic.secondbrain.core.designsystem.util.JalaliDate
import ir.dbsgraphic.secondbrain.core.designsystem.util.toPersianDigits

@Composable
fun GoalsRoute(
    onOpenItem: (String) -> Unit,
    viewModel: GoalsViewModel = hiltViewModel(),
) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()
    GoalsScreen(
        goals = goals,
        onLogProgress = viewModel::logProgress,
        onMarkAchieved = viewModel::markAchieved,
        onCreate = viewModel::create,
        onOpenItem = onOpenItem,
    )
}

@Composable
fun GoalsScreen(
    goals: List<GoalUi>,
    onLogProgress: (String) -> Unit,
    onMarkAchieved: (String) -> Unit,
    onCreate: (String, Int, Long?) -> Unit,
    onOpenItem: (String) -> Unit,
) {
    val colors = SecondBrainTheme.colors
    val type = SecondBrainTheme.type
    val space = SecondBrainTheme.spacing
    var showAdd by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
            )
            .padding(horizontal = space.xl),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (goals.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                    SbText(text = "هنوز هدفی نساخته‌ای.", style = type.title)
                    Spacer(Modifier.height(space.sm))
                    SbText(
                        text = "چیزی که می‌خواهی به آن برسی را بنویس و قدم‌به‌قدم پیش برو.",
                        style = type.body,
                        color = colors.muted,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = goals, key = { it.item.id }) { goal ->
                        GoalRow(
                            ui = goal,
                            onLogProgress = { onLogProgress(goal.item.id) },
                            onMarkAchieved = { onMarkAchieved(goal.item.id) },
                            onOpen = { onOpenItem(goal.item.id) },
                        )
                        SbHairline()
                    }
                }
            }
        }

        if (showAdd) {
            AddSheet(
                onDismiss = { showAdd = false },
                onCreate = { title, target, due ->
                    onCreate(title, target, due); showAdd = false
                },
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(SecondBrainTheme.shapes.large)
                    .background(colors.accent)
                    .clickable { showAdd = true }
                    .padding(vertical = space.lg),
                contentAlignment = Alignment.Center,
            ) {
                SbText(text = "افزودن هدف", style = type.label, color = colors.onAccent)
            }
        }
        Spacer(Modifier.height(space.md))
    }
}

@Composable
private fun GoalRow(ui: GoalUi, onLogProgress: () -> Unit, onMarkAchieved: () -> Unit, onOpen: () -> Unit) {
    val colors = SecondBrainTheme.colors
    val type = SecondBrainTheme.type
    val space = SecondBrainTheme.spacing

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = space.lg)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f).clickable(onClick = onOpen)) {
                SbText(text = ui.item.content, style = type.bodyLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                ui.dueAt?.let { due ->
                    Spacer(Modifier.height(space.xs))
                    SbText(text = "تا ${JalaliDate.formatDate(due)}", style = type.monoSmall, color = colors.muted)
                }
            }
            Spacer(Modifier.width(space.md))
            if (ui.details.isAchieved) {
                SbText(text = "رسیدی ✓", style = type.label, color = colors.accent)
            } else {
                Box(
                    modifier = Modifier
                        .clip(SecondBrainTheme.shapes.medium)
                        .background(colors.accent)
                        .clickable(onClick = if (ui.details.target == 1) onMarkAchieved else onLogProgress)
                        .padding(horizontal = space.lg, vertical = space.sm),
                ) {
                    SbText(
                        text = if (ui.details.target == 1) "انجام شد" else "+۱",
                        style = type.label,
                        color = colors.onAccent,
                    )
                }
            }
        }

        // Progress track (only meaningful for multi-step goals).
        if (ui.details.target > 1) {
            Spacer(Modifier.height(space.sm))
            ProgressBar(fraction = ui.details.progress)
            Spacer(Modifier.height(space.xs))
            SbText(
                text = "${ui.details.done.toString().toPersianDigits()} از ${ui.details.target.toString().toPersianDigits()}",
                style = type.monoSmall,
                color = colors.muted,
            )
        }
    }
}

@Composable
private fun ProgressBar(fraction: Float) {
    val colors = SecondBrainTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(SecondBrainTheme.shapes.small)
            .background(colors.hairline),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(SecondBrainTheme.shapes.small)
                .background(colors.accent),
        )
    }
}

private const val DAY_MS = 24L * 60 * 60 * 1000

private data class DuePreset(val label: String, val offsetDays: Int?)

private val duePresets = listOf(
    DuePreset("بدون مهلت", null),
    DuePreset("یک هفته", 7),
    DuePreset("یک ماه", 30),
    DuePreset("سه ماه", 90),
)

@Composable
private fun AddSheet(
    onDismiss: () -> Unit,
    onCreate: (String, Int, Long?) -> Unit,
) {
    val colors = SecondBrainTheme.colors
    val type = SecondBrainTheme.type
    val space = SecondBrainTheme.spacing

    var title by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var dueIndex by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SecondBrainTheme.shapes.large)
            .background(colors.surface)
            .padding(space.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SbText(text = "هدف جدید", style = type.bodyLarge)
            Spacer(Modifier.weight(1f))
            SbText(
                text = "بستن",
                style = type.label,
                color = colors.muted,
                modifier = Modifier.clickable(onClick = onDismiss),
            )
        }
        Spacer(Modifier.height(space.md))

        Field(value = title, onValueChange = { title = it }, placeholder = "می‌خواهم به…")
        Spacer(Modifier.height(space.sm))
        Field(
            value = target,
            onValueChange = { target = it.filter { c -> c.isDigit() } },
            placeholder = "تعداد گام‌ها (خالی = یک‌مرحله‌ای)",
            numeric = true,
        )

        Spacer(Modifier.height(space.md))
        SbText(text = "مهلت", style = type.caption, color = colors.muted)
        Spacer(Modifier.height(space.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(space.sm)) {
            duePresets.forEachIndexed { index, preset ->
                Toggle(label = preset.label, selected = dueIndex == index) { dueIndex = index }
            }
        }

        Spacer(Modifier.height(space.lg))
        val canAdd = title.isNotBlank()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(SecondBrainTheme.shapes.medium)
                .background(if (canAdd) colors.accent else colors.hairline)
                .clickable(enabled = canAdd) {
                    val due = duePresets[dueIndex].offsetDays?.let {
                        System.currentTimeMillis() + it * DAY_MS
                    }
                    onCreate(title.trim(), target.toIntOrNull() ?: 1, due)
                }
                .padding(vertical = space.md),
            contentAlignment = Alignment.Center,
        ) {
            SbText(
                text = "ثبت",
                style = type.label,
                color = if (canAdd) colors.onAccent else colors.muted,
            )
        }
    }
}

@Composable
private fun Toggle(label: String, selected: Boolean, onClick: () -> Unit) {
    val colors = SecondBrainTheme.colors
    val type = SecondBrainTheme.type
    val space = SecondBrainTheme.spacing
    Box(
        modifier = Modifier
            .clip(SecondBrainTheme.shapes.medium)
            .background(if (selected) colors.accent else colors.background)
            .clickable(onClick = onClick)
            .padding(horizontal = space.md, vertical = space.sm),
    ) {
        SbText(
            text = label,
            style = type.label,
            color = if (selected) colors.onAccent else colors.muted,
        )
    }
}

@Composable
private fun Field(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    numeric: Boolean = false,
) {
    val colors = SecondBrainTheme.colors
    val space = SecondBrainTheme.spacing
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(SecondBrainTheme.shapes.medium)
            .background(colors.background)
            .padding(horizontal = space.md, vertical = space.md),
    ) {
        SbTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            singleLine = true,
            keyboardOptions = if (numeric) {
                KeyboardOptions(keyboardType = KeyboardType.Number)
            } else {
                KeyboardOptions.Default
            },
        )
    }
}
