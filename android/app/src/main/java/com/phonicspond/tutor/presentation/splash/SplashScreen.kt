package com.phonicspond.tutor.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.di.AppViewModelFactory
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.PondDusk
import com.phonicspond.tutor.core.theme.PondNight
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.WaterDeep
import com.phonicspond.tutor.core.ui.PondBackground
import kotlin.math.sin

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.finished) {
        if (state.finished) {
            onFinished()
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splashLoops")
    val bob by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashBob"
    )
    val breath by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashBreath"
    )
    val ripple by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splashRipple"
    )
    val bubbles by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splashBubbles"
    )

    PondBackground(
        overlay = listOf(
            PondNight.copy(alpha = 0.94f),
            PondDusk.copy(alpha = 0.92f),
            FrogGreen.copy(alpha = 0.90f)
        ),
        modifier = modifier,
        blobTint = WaterDeep,
        blobStrength = 0.16f,
        rippleTint = WaterDeep
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.38f)
            listOf(0f, 0.33f, 0.66f).forEach { phase ->
                val progress = (ripple + phase) % 1f
                drawCircle(
                    color = WaterDeep.copy(alpha = 0.5f * (1f - progress)),
                    radius = 60.dp.toPx() + 90.dp.toPx() * progress,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            repeat(6) { index ->
                val phase = (bubbles + index * 0.17f) % 1f
                val x = size.width * (0.18f + index * 0.13f) + sin(phase * 6.28f) * 14.dp.toPx()
                val y = size.height * (1f - phase)
                drawCircle(
                    color = SurfaceWhite.copy(alpha = 0.22f * (1f - phase)),
                    radius = (4 + index).dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.logo_description),
                modifier = Modifier
                    .size(196.dp)
                    .graphicsLayer {
                        translationY = bob.dp.toPx()
                        scaleX = breath
                        scaleY = breath
                    },
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(28.dp))
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.displayMedium,
                    color = PondNight.copy(alpha = 0.35f),
                    modifier = Modifier.graphicsLayer {
                        translationX = 2f
                        translationY = 3f
                    }
                )
                Text(
                    text = stringResource(R.string.app_title),
                    style = MaterialTheme.typography.displayMedium,
                    color = SurfaceWhite
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelLarge,
                color = WaterDeep
            )
            Spacer(modifier = Modifier.height(28.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .width(180.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LilyPink,
                trackColor = SurfaceWhite.copy(alpha = 0.22f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = SurfaceWhite.copy(alpha = 0.6f)
            )
        }
    }
}
