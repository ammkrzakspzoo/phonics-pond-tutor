package com.phonicspond.tutor.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.TextPrimary

enum class LilyPadState {
    IDLE,
    HINTED,
    CORRECT
}

@Composable
fun LilyPadTile(
    letter: Char,
    padSize: Dp,
    state: LilyPadState,
    description: String,
    stateLabel: String,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val dip by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = tween(GameConfig.PAD_RIPPLE_MS, easing = FastOutSlowInEasing),
        label = "lilyPadDip"
    )
    val ripple by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = tween(GameConfig.PAD_RIPPLE_MS, easing = FastOutSlowInEasing),
        label = "lilyPadRipple"
    )
    val glowTransition = rememberInfiniteTransition(label = "lilyPadGlow")
    val glow by glowTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lilyPadGlowAlpha"
    )
    val fontSize = (padSize.value * 0.38f).coerceIn(34f, 56f)
    Box(
        modifier = modifier
            .size(padSize)
            .graphicsLayer {
                scaleX = dip
                scaleY = dip
                translationY = (1f - dip) * 60f
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onTap
            )
            .semantics {
                contentDescription = description
                stateDescription = stateLabel
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (state == LilyPadState.HINTED) {
                drawCircle(
                    color = LilyPink.copy(alpha = glow),
                    radius = size.minDimension * 0.46f,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
            if (state == LilyPadState.CORRECT) {
                drawCircle(
                    color = FrogGreen.copy(alpha = 0.35f),
                    radius = size.minDimension * 0.44f
                )
            }
            if (ripple > 0f) {
                drawCircle(
                    color = SurfaceWhite.copy(alpha = 0.6f * (1f - ripple)),
                    radius = size.minDimension * 0.2f + size.minDimension * 0.3f * ripple,
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }
        Text(
            text = letter.uppercaseChar().toString(),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.sp,
            color = TextPrimary
        )
        if (state == LilyPadState.CORRECT) {
            Icon(
                imageVector = AppIcons.Check,
                contentDescription = null,
                tint = SurfaceWhite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(28.dp)
                    .alpha(0.95f)
            )
        }
    }
}
