package ir.dbsgraphic.secondbrain.feature.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ir.dbsgraphic.secondbrain.core.data.WeeklyReview
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbCard
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbText
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbTextButton
import ir.dbsgraphic.secondbrain.core.designsystem.theme.SecondBrainTheme
import ir.dbsgraphic.secondbrain.core.designsystem.util.JalaliDate
import ir.dbsgraphic.secondbrain.core.designsystem.util.toPersianDigits

@Composable
fun WeeklyReviewRoute(
    onBack: () -> Unit,
    viewModel: WeeklyReviewViewModel = hiltViewModel(),
) {
    val review by viewModel.review.collectAsStateWithLifecycle()
    WeeklyReviewScreen(review = review, onBack = onBack)
}

@Composable
fun WeeklyReviewScreen(review: WeeklyReview, onBack: () -> Unit) {
    val colors = SecondBrainTheme.colors
    val type = SecondBrainTheme.type
    val space = SecondBrainTheme.spacing

    val now = System.currentTimeMillis()
    val weekAgo = now - 6L * 24 * 60 * 60 * 1000
    val rangeLabel = "${JalaliDate.formatDate(weekAgo)} — ${JalaliDate.formatDate(now)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = space.xl),
    ) {
        Spacer(Modifier.height(space.lg))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SbText(text = "مرور هفته", style = type.title)
            SbTextButton(label = "بازگشت", onClick = onBack)
        }
        Spacer(Modifier.height(space.xs))
        SbText(text = rangeLabel, style = type.monoSmall, color = colors.muted)
        Spacer(Modifier.height(space.lg))

        // ── Flow: what came in, what got organized ────────────────────────
        SbCard {
            SbText(text = "جریان", style = type.bodyLarge)
            Spacer(Modifier.height(space.md))
            StatRow(label = "ثبت‌شده این هفته", value = review.captured)
            StatRow(label = "سامان‌یافته", value = review.triaged)
            StatRow(label = "در صندوق، منتظر تصمیم", value = review.inbox, emphasizeWhenNonZero = true)
        }
        Spacer(Modifier.height(space.lg))

        // ── Consistency: habits & medicine ────────────────────────────────
        SbCard {
            SbText(text = "پایداری", style = type.bodyLarge)
            Spacer(Modifier.height(space.md))
            StatRow(label = "ثبت عادت‌ها و داروها", value = review.checkins)
            StatRow(label = "روزهای فعال", value = review.activeDays)
        }
        Spacer(Modifier.height(space.lg))

        // ── What's owed: reminders ────────────────────────────────────────
        SbCard {
            SbText(text = "سررسیدها", style = type.bodyLarge)
            Spacer(Modifier.height(space.md))
            StatRow(label = "پیش‌روی هفته‌ی آینده", value = review.upcomingReminders)
            StatRow(label = "گذشته و انجام‌نشده", value = review.overdueReminders, emphasizeWhenNonZero = true)
        }
        Spacer(Modifier.height(space.lg))

        // ── Goals ─────────────────────────────────────────────────────────
        SbCard {
            SbText(text = "هدف‌ها", style = type.bodyLarge)
            Spacer(Modifier.height(space.md))
            StatRow(label = "در حال پیگیری", value = review.goalsActive)
            StatRow(label = "محقق‌شده این هفته", value = review.goalsAchieved)
        }

        Spacer(Modifier.height(space.xl))
        SbText(
            text = "هفته‌ای یک‌بار اینجا را مرور کن: صندوق را خالی کن، سررسیدهای عقب‌افتاده را تعیین تکلیف کن، و یک قدم برای هدف‌هایت بردار.",
            style = type.body,
            color = colors.muted,
        )
        Spacer(Modifier.height(space.xl))
    }
}

@Composable
private fun StatRow(label: String, value: Int, emphasizeWhenNonZero: Boolean = false) {
    val colors = SecondBrainTheme.colors
    val type = SecondBrainTheme.type
    val space = SecondBrainTheme.spacing
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = space.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SbText(text = label, style = type.body, color = colors.muted)
        SbText(
            text = value.toString().toPersianDigits(),
            style = type.title,
            color = if (emphasizeWhenNonZero && value > 0) colors.accentSecondary else colors.text,
        )
    }
}
