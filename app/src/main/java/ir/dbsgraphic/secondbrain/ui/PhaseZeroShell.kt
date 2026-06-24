package ir.dbsgraphic.secondbrain.ui

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbHairline
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbPrimaryButton
import ir.dbsgraphic.secondbrain.core.designsystem.component.SbText
import ir.dbsgraphic.secondbrain.core.designsystem.theme.SecondBrainTheme

/**
 * Phase 0 shell. No features yet — this exists to prove the spine: RTL-native
 * layout, Yekan Bakh as the voice, Space Mono as the instrument, the Pine
 * Editorial palette, and a first taste of the Timeline signature (the spine
 * with an open marker) shown as an empty-state invitation, not decoration.
 */
@Composable
fun PhaseZeroShell() {
    val colors = SecondBrainTheme.colors
    val type = SecondBrainTheme.type
    val space = SecondBrainTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = space.xl, vertical = space.lg),
    ) {
        // Header — name in the human voice (right), build tag in the instrument
        // voice (left). RTL puts the first child on the right.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SbText(text = "مغز دوم", style = type.title)
            SbText(
                text = "v0.1 · PHASE 0",
                style = type.monoSmall,
                color = colors.muted,
            )
        }

        Spacer(Modifier.height(space.xxxl))

        // The manifesto, large, in the human voice.
        SbText(text = "کمتر به یاد آور،", style = type.display)
        SbText(text = "بیشتر زندگی کن.", style = type.display, color = colors.accent)

        Spacer(Modifier.height(space.lg))
        SbText(
            text = "همه چیز را ثبت کن. همه چیز را پیوند بده. هیچ چیز را فراموش نکن.",
            style = type.body,
            color = colors.muted,
        )

        Spacer(Modifier.height(space.xxl))
        SbHairline()
        Spacer(Modifier.height(space.xl))

        // A taste of the signature: the Timeline spine, here as an empty-state
        // invitation. The pine spine + open marker is the showpiece in miniature.
        SbText(text = "خط زمان", style = type.monoSmall, color = colors.muted)
        Spacer(Modifier.height(space.md))
        TimelineSpinePreview()

        Spacer(Modifier.weight(1f))

        SbText(
            text = "هنوز چیزی ثبت نشده. اولین فکرت را بنویس.",
            style = type.body,
            color = colors.muted,
        )
        Spacer(Modifier.height(space.md))
        SbPrimaryButton(label = "ثبت اولین فکر", onClick = { /* Phase 1: Capture */ })
    }
}

/**
 * The vertical pine spine with a single open marker — the Timeline's identity
 * in one row. In RTL the spine sits on the right and content flows left of it.
 */
@Composable
private fun TimelineSpinePreview() {
    val colors = SecondBrainTheme.colors
    val space = SecondBrainTheme.spacing

    Row(verticalAlignment = Alignment.CenterVertically) {
        // Spine + open marker (leading edge — the right in RTL). An open ring
        // reads as "waiting to be filled" — fitting for the empty state.
        Box(contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(colors.accent),
            )
            Box(
                Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(colors.accent)
                    .padding(3.dp),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(colors.background),
                )
            }
        }
        Spacer(Modifier.width(space.md))
        Column {
            SbText(text = "امروز", style = SecondBrainTheme.type.label)
            SbText(
                text = "جای اولین لحظه‌ی توست",
                style = SecondBrainTheme.type.monoSmall,
                color = colors.muted,
            )
        }
    }
}

@Preview(showBackground = true, locale = "fa")
@Composable
private fun PhaseZeroShellPreview() {
    SecondBrainTheme {
        PhaseZeroShell()
    }
}
