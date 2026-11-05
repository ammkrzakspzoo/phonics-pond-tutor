package com.phonicspond.tutor.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.SurfaceWhite

@Composable
fun PhonemeBubble(
    display: String,
    description: String,
    onReplay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "phonemeWaves")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phonemeWaveProgress"
    )
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .width(34.dp)
                .fillMaxHeight()
        ) {
            val origin = Offset(0f, size.height / 2f)
            listOf(0f, 0.33f, 0.66f).forEach { offset ->
                val progress = (wave + offset) % 1f
                drawCircle(
                    color = FrogGreen.copy(alpha = 0.45f * (1f - progress)),
                    radius = size.minDimension * (0.25f + progress * 0.9f),
                    center = origin,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
        ClayCard(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onReplay
                )
                .semantics {
                    contentDescription = description
                    liveRegion = LiveRegionMode.Polite
                },
            fill = listOf(SurfaceWhite.copy(alpha = 0.96f), SurfaceWhite.copy(alpha = 0.90f)),
            corner = 26.dp,
            elevation = 8.dp
        ) {
            Box(modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp)) {
                Text(
                    text = display,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = FrogGreen
                )
            }
        }
    }
}
