package com.phonicspond.tutor.presentation.menu

import com.phonicspond.tutor.core.ui.AppIcons
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.di.AppViewModelFactory
import com.phonicspond.tutor.core.theme.FrogDark
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.FrogLight
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.RewardGold
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.TextOnPrimary
import com.phonicspond.tutor.core.theme.TextPrimary
import com.phonicspond.tutor.core.theme.TextSecondary
import com.phonicspond.tutor.core.theme.WaterDeep
import com.phonicspond.tutor.core.theme.WaterLight
import com.phonicspond.tutor.core.ui.BentoTile
import com.phonicspond.tutor.core.ui.ClayCard
import com.phonicspond.tutor.core.ui.PondBackground
import com.phonicspond.tutor.core.ui.PondButton
import com.phonicspond.tutor.core.ui.StatCard
import com.phonicspond.tutor.domain.model.ActivityKind

@Composable
fun MenuScreen(
    onPlay: (ActivityKind) -> Unit,
    onBlend: () -> Unit,
    onTrace: () -> Unit,
    onStickers: () -> Unit,
    onGrownUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MenuViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    MenuContent(
        state = state,
        onPlay = onPlay,
        onBlend = onBlend,
        onTrace = onTrace,
        onStickers = onStickers,
        onGrownUp = onGrownUp,
        modifier = modifier
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: (ActivityKind) -> Unit,
    onBlend: () -> Unit,
    onTrace: () -> Unit,
    onStickers: () -> Unit,
    onGrownUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        revealed = true
    }

    val breathTransition = rememberInfiniteTransition(label = "menuBreath")
    val breath by breathTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "menuBreathScale"
    )

    PondBackground(
        overlay = listOf(
            WaterLight.copy(alpha = 0.94f),
            WaterDeep.copy(alpha = 0.90f),
            WaterDeep.copy(alpha = 0.94f)
        ),
        modifier = modifier,
        blobTint = LilyPink,
        blobStrength = 0.14f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            MenuHeader(stickers = state.stickers)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.ready_prompt),
                style = MaterialTheme.typography.titleLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PlayTile(
                    nextActivity = state.nextActivity,
                    onPlay = { onPlay(state.nextActivity) },
                    modifier = Modifier
                        .weight(1.6f)
                        .fillMaxSize()
                        .staggered(revealed, 0)
                        .graphicsLayer {
                            scaleX = breath
                            scaleY = breath
                        }
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    BentoTile(
                        label = stringResource(R.string.blend),
                        description = stringResource(R.string.blend_description),
                        onClick = onBlend,
                        painter = painterResource(R.drawable.sprite_3),
                        markSize = 42.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .staggered(revealed, 70)
                    )
                    BentoTile(
                        label = stringResource(R.string.trace),
                        description = stringResource(R.string.trace_description),
                        onClick = onTrace,
                        painter = painterResource(R.drawable.sprite_5),
                        markSize = 42.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .staggered(revealed, 140)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                BentoTile(
                    label = stringResource(R.string.stickers),
                    description = stringResource(R.string.stickers_description),
                    onClick = onStickers,
                    painter = painterResource(R.drawable.sprite_4),
                    markSize = 40.dp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .staggered(revealed, 210)
                )
                BentoTile(
                    label = stringResource(R.string.words),
                    description = stringResource(R.string.words_description),
                    onClick = { onPlay(ActivityKind.BLEND) },
                    painter = painterResource(R.drawable.sprite_2),
                    markSize = 36.dp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .staggered(revealed, 250)
                )
                BentoTile(
                    label = stringResource(R.string.grown_ups),
                    description = stringResource(R.string.grown_ups_description),
                    onClick = onGrownUp,
                    icon = AppIcons.Lock,
                    iconTint = TextSecondary,
                    markSize = 40.dp,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .staggered(revealed, 290)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            MenuStats(state = state, modifier = Modifier.staggered(revealed, 330))
        }
    }
}

@Composable
private fun MenuHeader(stickers: Int) {
    val stickersDescription = stringResource(R.string.stickers_count_description, stickers)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        }
        ClayCard(
            corner = 20.dp,
            elevation = 6.dp,
            fill = listOf(SurfaceWhite.copy(alpha = 0.90f), SurfaceWhite.copy(alpha = 0.80f)),
            borderTint = FrogGreen.copy(alpha = 0.25f),
            modifier = Modifier
                .height(40.dp)
                .semantics {
                    contentDescription = stickersDescription
                }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.Star,
                    contentDescription = null,
                    tint = LilyPink,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stickers.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun PlayTile(
    nextActivity: ActivityKind,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activityLabel = when (nextActivity) {
        ActivityKind.SOUND_HOP -> stringResource(R.string.sound_hop_title)
        ActivityKind.BLEND -> stringResource(R.string.blend_title)
        ActivityKind.TRACE -> stringResource(R.string.trace_title)
    }
    ClayCard(
        modifier = modifier,
        fill = listOf(FrogLight, FrogGreen, FrogDark),
        corner = 30.dp,
        elevation = 14.dp,
        borderTint = SurfaceWhite.copy(alpha = 0.25f)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = AppIcons.Star,
                contentDescription = null,
                tint = LilyPink,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(14.dp)
                    .alpha(0.9f)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.frog_description),
                    modifier = Modifier.size(84.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = activityLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = TextOnPrimary.copy(alpha = 0.88f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                PondButton(
                    label = stringResource(R.string.play),
                    onClick = onPlay,
                    icon = AppIcons.PlayArrow,
                    height = 56.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun MenuStats(state: MenuUiState, modifier: Modifier = Modifier) {
    val visible = listOf(
        Triple(stringResource(R.string.letters), state.masteredLetters, FrogGreen),
        Triple(stringResource(R.string.words), state.wordsRead, LilyPink),
        Triple(stringResource(R.string.stickers), state.stickers, RewardGold)
    ).filter { it.second > 0 }

    if (visible.size < 2) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
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

@Composable
private fun Modifier.staggered(revealed: Boolean, delayMillis: Int): Modifier {
    val progress by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 320,
            delayMillis = delayMillis,
            easing = FastOutSlowInEasing
        ),
        label = "menuStagger"
    )
    return this
        .alpha(progress)
        .graphicsLayer {
            translationY = (1f - progress) * 16.dp.toPx()
        }
}
