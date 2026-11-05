package com.phonicspond.tutor.presentation.game

import com.phonicspond.tutor.core.ui.AppIcons
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.core.di.AppViewModelFactory
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.TextPrimary
import com.phonicspond.tutor.core.theme.WaterBase
import com.phonicspond.tutor.core.theme.WaterDeep
import com.phonicspond.tutor.core.theme.WaterLight
import com.phonicspond.tutor.core.ui.ClayCard
import com.phonicspond.tutor.core.ui.FloatingPillBar
import com.phonicspond.tutor.core.ui.LilyPadState
import com.phonicspond.tutor.core.ui.LilyPadTile
import com.phonicspond.tutor.core.ui.PhonemeBubble
import com.phonicspond.tutor.core.ui.PondBackground
import com.phonicspond.tutor.core.ui.PondHeader
import com.phonicspond.tutor.domain.model.LetterCard
import com.phonicspond.tutor.presentation.dialog.PauseDialog
import kotlin.math.sin

@Composable
fun GameScreen(
    onHome: () -> Unit,
    onFinished: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GameViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    var paused by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(Unit) {
        viewModel.onEnter()
        onDispose { viewModel.onLeave() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.Finished -> onFinished(event.wordId, event.stickerEarned)
            }
        }
    }

    LaunchedEffect(state.cueTick) {
        if (state.cueTick > 0 && state.hapticsEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    GameContent(
        state = state,
        onHome = onHome,
        onPause = {
            paused = true
            viewModel.onLeave()
        },
        onPadTap = viewModel::onPadTap,
        onReplayCue = viewModel::onReplayCue,
        onHint = viewModel::onHintRequested,
        modifier = modifier
    )

    if (paused) {
        PauseDialog(
            onKeepPlaying = {
                paused = false
                viewModel.onEnter()
            },
            onMenu = onHome
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    onHome: () -> Unit,
    onPause: () -> Unit,
    onPadTap: (Char) -> Unit,
    onReplayCue: () -> Unit,
    onHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    PondBackground(
        overlay = listOf(
            WaterLight.copy(alpha = 0.90f),
            WaterBase.copy(alpha = 0.88f),
            WaterDeep.copy(alpha = 0.92f)
        ),
        modifier = modifier,
        blobTint = FrogGreen,
        blobStrength = 0.10f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            PondHeader(
                title = stringResource(R.string.sound_hop_title),
                homeDescription = stringResource(R.string.home_description),
                onHome = onHome
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressPill(current = state.itemIndex + 1, total = state.itemCount)
                    Spacer(modifier = Modifier.width(6.dp))
                    PauseAction(onPause = onPause)
                }
            }
            PromptBlock(
                state = state,
                onReplayCue = onReplayCue
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LilyPadBoard(
                    state = state,
                    onPadTap = onPadTap,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
                FloatingPillBar(
                    firstLabel = stringResource(R.string.hear_it),
                    firstIcon = AppIcons.Refresh,
                    onFirst = onReplayCue,
                    secondLabel = stringResource(R.string.hint),
                    secondIcon = AppIcons.Info,
                    onSecond = onHint,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun PauseAction(onPause: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onPause
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.icon_pause),
            contentDescription = stringResource(R.string.pause_description),
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun ProgressPill(current: Int, total: Int) {
    ClayCard(
        corner = 18.dp,
        elevation = 6.dp,
        fill = listOf(SurfaceWhite.copy(alpha = 0.92f), SurfaceWhite.copy(alpha = 0.82f)),
        modifier = Modifier.height(38.dp)
    ) {
        Text(
            text = stringResource(R.string.round_progress, current, total),
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .semantics { liveRegion = LiveRegionMode.Polite }
        )
    }
}

@Composable
private fun PromptBlock(
    state: GameUiState,
    onReplayCue: () -> Unit
) {
    val leaping = state.solvedLetter != null
    val leap by animateFloatAsState(
        targetValue = if (leaping) 1f else 0f,
        animationSpec = tween(GameConfig.FROG_LEAP_MS, easing = FastOutSlowInEasing),
        label = "frogLeap"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(168.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.frog_description),
            modifier = Modifier
                .size(112.dp)
                .graphicsLayer {
                    translationY = -sin(leap * 3.14f) * 70.dp.toPx()
                    translationX = sin(leap * 3.14f) * 10.dp.toPx()
                },
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(4.dp))
        PhonemeBubble(
            display = stringResource(R.string.phoneme_format, state.phoneme),
            description = stringResource(R.string.cue_description, state.phoneme),
            onReplay = onReplayCue,
            modifier = Modifier.height(96.dp)
        )
    }
}

@Composable
private fun LilyPadBoard(
    state: GameUiState,
    onPadTap: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boardPad = 20.dp
        val gap = 18.dp
        val byWidth = (maxWidth - boardPad * 2 - gap) / 2
        val byHeight = (maxHeight - boardPad * 2 - gap - 104.dp) / 2
        val padSize = minOf(byWidth, byHeight, 150.dp).coerceAtLeast(72.dp)
        val rows = state.options.chunked(2)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = boardPad),
            verticalArrangement = Arrangement.spacedBy(gap),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    row.forEach { card ->
                        PadForCard(
                            card = card,
                            state = state,
                            padSize = padSize,
                            onPadTap = onPadTap
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PadForCard(
    card: LetterCard,
    state: GameUiState,
    padSize: androidx.compose.ui.unit.Dp,
    onPadTap: (Char) -> Unit
) {
    val padState = when {
        state.solvedLetter == card.letter -> LilyPadState.CORRECT
        state.hinted && state.correctLetter == card.letter -> LilyPadState.HINTED
        else -> LilyPadState.IDLE
    }
    val stateLabel = when (padState) {
        LilyPadState.CORRECT -> stringResource(R.string.pad_state_correct)
        LilyPadState.HINTED -> stringResource(R.string.pad_state_hinted)
        LilyPadState.IDLE -> stringResource(R.string.pad_state_idle)
    }
    LilyPadTile(
        letter = card.letter,
        padSize = padSize,
        state = padState,
        description = stringResource(R.string.pad_description, card.letter.uppercaseChar().toString()),
        stateLabel = stateLabel,
        onTap = { onPadTap(card.letter) }
    )
}
