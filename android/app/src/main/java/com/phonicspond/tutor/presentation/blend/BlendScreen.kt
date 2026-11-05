package com.phonicspond.tutor.presentation.blend

import com.phonicspond.tutor.core.ui.AppIcons
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.phonicspond.tutor.core.ui.SparkleTrail

@Composable
fun BlendScreen(
    onHome: () -> Unit,
    onFinished: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: BlendViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    DisposableEffect(Unit) {
        viewModel.onEnter()
        onDispose { viewModel.onLeave() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BlendEvent.Finished -> onFinished(event.wordId, event.stickerEarned)
            }
        }
    }

    LaunchedEffect(state.cueTick) {
        if (state.cueTick > 0 && state.hapticsEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    BlendContent(
        state = state,
        onHome = onHome,
        onPadCrossed = viewModel::onPadCrossed,
        onReplayCue = viewModel::onReplayCue,
        onHint = viewModel::onHintRequested,
        modifier = modifier
    )
}

@Composable
private fun BlendContent(
    state: BlendUiState,
    onHome: () -> Unit,
    onPadCrossed: (Int) -> Unit,
    onReplayCue: () -> Unit,
    onHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    PondBackground(
        overlay = listOf(
            WaterLight.copy(alpha = 0.92f),
            WaterBase.copy(alpha = 0.88f),
            WaterDeep.copy(alpha = 0.94f)
        ),
        modifier = modifier,
        blobTint = LilyPink,
        blobStrength = 0.12f
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp)
        ) {
            PondHeader(
                title = stringResource(R.string.blend_title),
                homeDescription = stringResource(R.string.home_description),
                onHome = onHome
            ) {
                ClayCard(
                    corner = 18.dp,
                    elevation = 6.dp,
                    fill = listOf(SurfaceWhite.copy(alpha = 0.92f), SurfaceWhite.copy(alpha = 0.82f)),
                    modifier = Modifier.height(38.dp)
                ) {
                    Text(
                        text = stringResource(
                            R.string.round_progress,
                            state.crossed,
                            state.letters.size.coerceAtLeast(1)
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = TextPrimary,
                        modifier = Modifier
                            .padding(horizontal = 14.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                PhonemeBubble(
                    display = currentCue(state),
                    description = stringResource(R.string.blend_cue_description, state.word),
                    onReplay = onReplayCue,
                    modifier = Modifier.height(92.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                BlendBridge(
                    state = state,
                    onPadCrossed = onPadCrossed,
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
private fun currentCue(state: BlendUiState): String {
    val pattern = stringResource(R.string.phoneme_format)
    val sound = state.phonemes.getOrNull(state.crossed.coerceAtMost(state.phonemes.lastIndex))
    return if (state.crossed >= state.letters.size && state.word.isNotEmpty()) {
        state.word
    } else {
        pattern.format(sound.orEmpty())
    }
}

@Composable
private fun BlendBridge(
    state: BlendUiState,
    onPadCrossed: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val trail = remember { mutableStateListOf<Offset>() }
    val density = LocalDensity.current
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boardPad = 16.dp
        val gap = 12.dp
        val columns = state.letters.size.coerceAtLeast(1)
        val byWidth = (maxWidth - boardPad * 2 - gap * (columns - 1)) / columns
        val byHeight = maxHeight - boardPad * 2 - 110.dp
        val padSize = minOf(byWidth, byHeight, 112.dp).coerceAtLeast(64.dp)
        val boardWidth = padSize * columns + gap * (columns - 1)
        val padPx = with(density) { padSize.toPx() }
        val gapPx = with(density) { gap.toPx() }
        val idleLabel = stringResource(R.string.pad_state_idle)
        val crossedLabel = stringResource(R.string.pad_state_correct)
        val hintedLabel = stringResource(R.string.pad_state_hinted)

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = boardPad)
                .width(boardWidth)
                .height(padSize)
                .pointerInput(state.letters) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            trail.clear()
                            trail.add(offset)
                            onPadCrossed(indexFor(offset.x, padPx, gapPx))
                        },
                        onDragEnd = { trail.clear() },
                        onDragCancel = { trail.clear() },
                        onDrag = { change, _ ->
                            trail.add(change.position)
                            if (trail.size > 40) {
                                trail.removeAt(0)
                            }
                            onPadCrossed(indexFor(change.position.x, padPx, gapPx))
                        }
                    )
                }
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                state.letters.forEachIndexed { index, letter ->
                    val padState = when {
                        index < state.crossed -> LilyPadState.CORRECT
                        state.hinted && index == state.crossed -> LilyPadState.HINTED
                        else -> LilyPadState.IDLE
                    }
                    val label = when (padState) {
                        LilyPadState.CORRECT -> crossedLabel
                        LilyPadState.HINTED -> hintedLabel
                        LilyPadState.IDLE -> idleLabel
                    }
                    LilyPadTile(
                        letter = letter,
                        padSize = padSize,
                        state = padState,
                        description = stringResource(
                            R.string.pad_description,
                            letter.uppercaseChar().toString()
                        ),
                        stateLabel = label,
                        onTap = { onPadCrossed(index) }
                    )
                }
            }
            SparkleTrail(
                points = trail.toList(),
                modifier = Modifier.fillMaxSize()
            )
        }
        if (state.crossed >= state.letters.size && state.word.isNotEmpty()) {
            ClayCard(
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(96.dp),
                corner = 30.dp,
                elevation = 14.dp,
                fill = listOf(SurfaceWhite.copy(alpha = 0.96f), SurfaceWhite.copy(alpha = 0.88f))
            ) {
                Text(
                    text = state.word,
                    style = MaterialTheme.typography.displaySmall,
                    color = FrogGreen,
                    modifier = Modifier.padding(horizontal = 28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.size(0.dp))
    }
}

private fun indexFor(x: Float, padPx: Float, gapPx: Float): Int {
    val stride = padPx + gapPx
    if (stride <= 0f) return -1
    return (x / stride).toInt()
}
