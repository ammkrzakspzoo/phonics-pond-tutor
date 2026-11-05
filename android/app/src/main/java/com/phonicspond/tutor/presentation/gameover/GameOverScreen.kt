package com.phonicspond.tutor.presentation.gameover

import com.phonicspond.tutor.core.ui.AppIcons
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.core.di.AppViewModelFactory
import com.phonicspond.tutor.core.navigation.Screen
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.RewardGold
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.TextPrimary
import com.phonicspond.tutor.core.theme.TextSecondary
import com.phonicspond.tutor.core.theme.WaterBlush
import com.phonicspond.tutor.core.theme.WaterDeep
import com.phonicspond.tutor.core.theme.WaterBase
import com.phonicspond.tutor.core.ui.ClayCard
import com.phonicspond.tutor.core.ui.PondBackground
import com.phonicspond.tutor.core.ui.PondButton
import com.phonicspond.tutor.core.ui.SoftButton
import com.phonicspond.tutor.core.ui.StatCard
import com.phonicspond.tutor.domain.model.ActivityKind
import kotlin.math.sin

@Composable
fun GameOverScreen(
    route: Screen.GameOver,
    onPlayAgain: (ActivityKind) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(route) {
        viewModel.show(route.wordId, route.activity, route.stickerEarned)
    }

    GameOverContent(
        state = state,
        onPlayAgain = { onPlayAgain(state.nextActivity) },
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(state.word) {
        revealed = state.word.isNotEmpty()
    }

    val headingScale by animateFloatAsState(
        targetValue = if (revealed) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
        label = "headingScale"
    )
    val reveal by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(
            durationMillis = (state.word.length.coerceAtLeast(1) * GameConfig.REVEAL_STEP_MS).toInt() + 240,
            easing = LinearEasing
        ),
        label = "wordReveal"
    )
    val petalTransition = rememberInfiniteTransition(label = "petals")
    val petalProgress by petalTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "petalFall"
    )

    PondBackground(
        overlay = listOf(
            WaterBlush.copy(alpha = 0.93f),
            WaterBase.copy(alpha = 0.90f),
            WaterDeep.copy(alpha = 0.94f)
        ),
        modifier = modifier,
        blobTint = LilyPink,
        blobStrength = 0.18f
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            repeat(14) { index ->
                val phase = (petalProgress + index * 0.071f) % 1f
                val x = size.width * (0.06f + index * 0.066f) + sin(phase * 6.28f + index) * 18.dp.toPx()
                val y = size.height * phase
                drawCircle(
                    color = LilyPink.copy(alpha = 0.5f * (1f - phase)),
                    radius = (5 + index % 5).dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.nice_reading),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                modifier = Modifier.scale(headingScale)
            )
            Spacer(modifier = Modifier.height(18.dp))
            WordCard(state = state, reveal = reveal)
            if (state.stickerEarned) {
                Spacer(modifier = Modifier.height(16.dp))
                StickerAward(revealed = revealed)
            }
            Spacer(modifier = Modifier.height(18.dp))
            GameOverStats(state = state)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.keep_going),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            PondButton(
                label = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                icon = AppIcons.PlayArrow,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            SoftButton(
                label = stringResource(R.string.menu),
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun WordCard(state: GameOverUiState, reveal: Float) {
    val picture = when (state.spriteIndex) {
        0 -> R.drawable.sprite_3
        1 -> R.drawable.sprite_2
        else -> R.drawable.sprite_5
    }
    val phonemePattern = stringResource(R.string.phoneme_format)
    val pronunciation = state.phonemes.joinToString(separator = " ") { sound ->
        phonemePattern.format(sound)
    }
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        fill = listOf(SurfaceWhite.copy(alpha = 0.96f), SurfaceWhite.copy(alpha = 0.88f)),
        corner = 32.dp,
        elevation = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(picture),
                contentDescription = stringResource(R.string.word_picture_description, state.word),
                modifier = Modifier.size(108.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                state.word.forEachIndexed { index, letter ->
                    val letterAlpha = (reveal * state.word.length - index).coerceIn(0f, 1f)
                    Text(
                        text = letter.toString(),
                        fontSize = 46.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp,
                        color = FrogGreen,
                        modifier = Modifier.alpha(letterAlpha)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = pronunciation,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun StickerAward(revealed: Boolean) {
    val drop by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "stickerDrop"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.sprite_4),
            contentDescription = stringResource(R.string.sticker_description),
            modifier = Modifier
                .size(84.dp)
                .scale(drop),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.sticker_earned),
            style = MaterialTheme.typography.labelLarge,
            color = LilyPink
        )
    }
}

@Composable
private fun GameOverStats(state: GameOverUiState) {
    val visible = listOf(
        Triple(stringResource(R.string.words), state.wordsRead, FrogGreen),
        Triple(stringResource(R.string.stickers), state.stickers, RewardGold),
        Triple(stringResource(R.string.letters), state.masteredLetters, LilyPink)
    ).filter { it.second > 0 }

    if (visible.size < 2) return

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        visible.forEach { entry ->
            StatCard(
                label = entry.first,
                value = entry.second.toString(),
                accent = entry.third,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
