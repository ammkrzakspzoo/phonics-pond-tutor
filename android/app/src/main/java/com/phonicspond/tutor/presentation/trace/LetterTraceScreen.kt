package com.phonicspond.tutor.presentation.trace

import com.phonicspond.tutor.core.ui.AppIcons
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.core.di.AppViewModelFactory
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.FrogLight
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.TextSecondary
import com.phonicspond.tutor.core.theme.WaterBase
import com.phonicspond.tutor.core.theme.WaterDeep
import com.phonicspond.tutor.core.theme.WaterLight
import com.phonicspond.tutor.core.ui.ClayCard
import com.phonicspond.tutor.core.ui.FloatingPillBar
import com.phonicspond.tutor.core.ui.PhonemeBubble
import com.phonicspond.tutor.core.ui.PondBackground
import com.phonicspond.tutor.core.ui.PondHeader
import com.phonicspond.tutor.domain.model.ActivityPhase
import com.phonicspond.tutor.domain.model.TracePoint

@Composable
fun LetterTraceScreen(
    onHome: () -> Unit,
    onFinished: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TraceViewModel = viewModel(factory = AppViewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    DisposableEffect(Unit) {
        viewModel.onEnter()
        onDispose { viewModel.onLeave() }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TraceEvent.Finished -> onFinished(event.wordId, event.stickerEarned)
            }
        }
    }

    LaunchedEffect(state.cueTick) {
        if (state.cueTick > 0 && state.hapticsEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    TraceContent(
        state = state,
        onHome = onHome,
        onStrokeStart = viewModel::onStrokeStart,
        onStrokePoint = viewModel::onStrokePoint,
        onClear = viewModel::onClear,
        onHint = viewModel::onHintRequested,
        modifier = modifier
    )
}

@Composable
private fun TraceContent(
    state: TraceUiState,
    onHome: () -> Unit,
    onStrokeStart: () -> Unit,
    onStrokePoint: (TracePoint, Float) -> Unit,
    onClear: () -> Unit,
    onHint: () -> Unit,
    modifier: Modifier = Modifier
) {
    PondBackground(
        overlay = listOf(
            WaterBase.copy(alpha = 0.92f),
            WaterLight.copy(alpha = 0.90f),
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
                title = stringResource(R.string.trace_title),
                homeDescription = stringResource(R.string.home_description),
                onHome = onHome
            ) {
                Text(
                    text = stringResource(
                        R.string.round_progress,
                        state.reached,
                        state.target.coerceAtLeast(1)
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
            Text(
                text = stringResource(R.string.trace_prompt),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            PhonemeBubble(
                display = stringResource(R.string.phoneme_format, state.phoneme),
                description = stringResource(R.string.cue_description, state.phoneme),
                onReplay = onHint,
                modifier = Modifier.height(84.dp)
            )
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val boardSide = minOf(maxWidth, maxHeight - BOARD_RESERVE).coerceAtLeast(160.dp)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                ) {
                    TraceBoard(
                        state = state,
                        onStrokeStart = onStrokeStart,
                        onStrokePoint = onStrokePoint,
                        modifier = Modifier.size(boardSide)
                    )
                }
                FloatingPillBar(
                    firstLabel = stringResource(R.string.clear),
                    firstIcon = AppIcons.Clear,
                    onFirst = onClear,
                    secondLabel = stringResource(R.string.hint),
                    secondIcon = AppIcons.Info,
                    onSecond = onHint,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun TraceBoard(
    state: TraceUiState,
    onStrokeStart: () -> Unit,
    onStrokePoint: (TracePoint, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val done = state.phase == ActivityPhase.CELEBRATING || state.phase == ActivityPhase.FINISHED
    val fill by animateFloatAsState(
        targetValue = if (done) 1f else 0f,
        animationSpec = tween(GameConfig.FROG_LEAP_MS, easing = FastOutSlowInEasing),
        label = "traceFill"
    )
    val hintTransition = rememberInfiniteTransition(label = "traceHint")
    val halo by hintTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "traceHalo"
    )
    val boardDescription = stringResource(R.string.trace_board_description, state.letter.toString())

    ClayCard(
        modifier = modifier,
        fill = listOf(SurfaceWhite.copy(alpha = 0.95f), SurfaceWhite.copy(alpha = 0.86f)),
        corner = 32.dp,
        elevation = 14.dp
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp)
                .semantics { contentDescription = boardDescription }
                .pointerInput(state.letter) {
                    detectDragGestures(
                        onDragStart = { onStrokeStart() },
                        onDrag = { change, _ ->
                            val width = size.width.toFloat()
                            val height = size.height.toFloat()
                            if (width > 0f && height > 0f) {
                                onStrokePoint(
                                    TracePoint(
                                        x = change.position.x / width,
                                        y = change.position.y / height
                                    ),
                                    GameConfig.TRACE_WAYPOINT_RADIUS_DP.dp.toPx() / width
                                )
                            }
                        }
                    )
                }
        ) {
            val guide = state.waypoints.map { Offset(it.x * size.width, it.y * size.height) }
            guide.zipWithNext { from, to ->
                drawLine(
                    color = WaterDeep,
                    start = from,
                    end = to,
                    strokeWidth = 24.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            guide.forEachIndexed { index, point ->
                val visited = index < state.reached
                val isNext = index == state.reached
                val tint = when {
                    visited -> FrogGreen
                    isNext && state.hinted -> LilyPink.copy(alpha = halo)
                    isNext -> LilyPink
                    else -> WaterLight
                }
                drawCircle(
                    color = tint,
                    radius = if (isNext) 9.dp.toPx() else 6.dp.toPx(),
                    center = point
                )
            }
            val stroke = state.stroke.map { Offset(it.x * size.width, it.y * size.height) }
            stroke.zipWithNext { from, to ->
                drawLine(
                    color = FrogGreen,
                    start = from,
                    end = to,
                    strokeWidth = 14.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            if (fill > 0f) {
                guide.zipWithNext { from, to ->
                    drawLine(
                        color = FrogLight.copy(alpha = fill),
                        start = from,
                        end = to,
                        strokeWidth = 20.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(
                    color = FrogGreen.copy(alpha = 0.3f * fill),
                    radius = size.minDimension * 0.46f,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}

private val BOARD_RESERVE = 104.dp
