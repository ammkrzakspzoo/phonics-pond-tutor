package com.phonicspond.tutor.presentation.stickers

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.di.AppViewModelFactory
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.RewardGold
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.TextSecondary
import com.phonicspond.tutor.core.theme.WaterBase
import com.phonicspond.tutor.core.theme.WaterDeep
import com.phonicspond.tutor.core.theme.WaterLight
import com.phonicspond.tutor.core.ui.ClayCard
import com.phonicspond.tutor.core.ui.PondHeader
import com.phonicspond.tutor.core.ui.PondBackground
import com.phonicspond.tutor.core.ui.StatCard

@Composable
fun StickerLilyScreen(
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: StickerViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    StickerContent(
        state = state,
        onHome = onHome,
        modifier = modifier
    )
}

@Composable
private fun StickerContent(
    state: StickerUiState,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "stickerLoops")
    val breath by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stickerBreath"
    )

    PondBackground(
        overlay = listOf(
            WaterLight.copy(alpha = 0.92f),
            WaterBase.copy(alpha = 0.90f),
            WaterDeep.copy(alpha = 0.94f)
        ),
        modifier = modifier,
        blobTint = RewardGold,
        blobStrength = 0.14f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            PondHeader(
                title = stringResource(R.string.stickers),
                homeDescription = stringResource(R.string.home_description),
                onHome = onHome
            ) {
                Text(
                    text = stringResource(R.string.sticker_tally, state.collected, state.total),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            Text(
                text = stringResource(R.string.sticker_prompt),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items((0 until state.total).toList()) { index ->
                    StickerSlot(
                        earned = index < state.collected,
                        breath = breath
                    )
                }
            }
            StickerStats(state = state)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StickerSlot(earned: Boolean, breath: Float) {
    val description = if (earned) {
        stringResource(R.string.sticker_description)
    } else {
        stringResource(R.string.sticker_empty_description)
    }
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        fill = if (earned) {
            listOf(SurfaceWhite.copy(alpha = 0.96f), SurfaceWhite.copy(alpha = 0.84f))
        } else {
            listOf(SurfaceWhite.copy(alpha = 0.55f), SurfaceWhite.copy(alpha = 0.40f))
        },
        corner = 24.dp,
        elevation = if (earned) 10.dp else 2.dp,
        borderTint = if (earned) LilyPink.copy(alpha = 0.45f) else WaterDeep.copy(alpha = 0.55f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = description,
                modifier = Modifier
                    .size(58.dp)
                    .alpha(if (earned) 1f else 0.2f)
                    .scale(if (earned) breath else 1f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun StickerStats(state: StickerUiState) {
    val visible = listOf(
        Triple(stringResource(R.string.stickers), state.collected, RewardGold),
        Triple(stringResource(R.string.words), state.wordsRead, FrogGreen),
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
