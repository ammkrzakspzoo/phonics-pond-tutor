package com.phonicspond.tutor.presentation.trace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.domain.model.ActivityKind
import com.phonicspond.tutor.domain.model.ActivityPhase
import com.phonicspond.tutor.domain.model.PhonicsPlan
import com.phonicspond.tutor.domain.model.TracePoint
import com.phonicspond.tutor.domain.repository.ContentRepository
import com.phonicspond.tutor.domain.repository.ProgressRepository
import com.phonicspond.tutor.domain.repository.SettingsRepository
import com.phonicspond.tutor.domain.usecase.AwardStickerUseCase
import com.phonicspond.tutor.domain.usecase.BlendWordUseCase
import com.phonicspond.tutor.domain.usecase.TraceProgressUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil

class TraceViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val contentRepository: ContentRepository,
    private val traceProgressUseCase: TraceProgressUseCase,
    private val blendWordUseCase: BlendWordUseCase,
    private val awardStickerUseCase: AwardStickerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TraceUiState())
    val state: StateFlow<TraceUiState> = _state.asStateFlow()

    private val eventChannel = Channel<TraceEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var timerJob: Job? = null

    fun onEnter() {
        if (_state.value.waypoints.isEmpty() || _state.value.phase == ActivityPhase.FINISHED) {
            startGlyph()
        } else {
            armTimers()
        }
    }

    fun onLeave() {
        timerJob?.cancel()
        timerJob = null
    }

    fun onStrokeStart() {
        val current = _state.value
        if (current.phase == ActivityPhase.CELEBRATING || current.phase == ActivityPhase.FINISHED) {
            return
        }
        _state.update { it.copy(phase = ActivityPhase.ACTIVE) }
    }

    fun onStrokePoint(point: TracePoint, radius: Float) {
        val current = _state.value
        if (current.phase == ActivityPhase.CELEBRATING || current.phase == ActivityPhase.FINISHED) {
            return
        }
        val reached = traceProgressUseCase(current.waypoints, current.reached, point, radius)
        _state.update { it.copy(stroke = it.stroke + point, reached = reached) }
        if (reached >= current.target && current.target > 0) {
            celebrate()
        }
    }

    fun onClear() {
        val current = _state.value
        if (current.phase == ActivityPhase.CELEBRATING || current.phase == ActivityPhase.FINISHED) {
            return
        }
        _state.update {
            it.copy(
                stroke = emptyList(),
                reached = 0,
                phase = ActivityPhase.LISTENING,
                cueTick = it.cueTick + 1
            )
        }
        armTimers()
    }

    fun onHintRequested() {
        _state.update { it.copy(hinted = true, phase = ActivityPhase.HINTING) }
    }

    private fun startGlyph() {
        val progress = progressRepository.progress.value
        val letter = PhonicsPlan.focusLetter(progress.masteredLetters)
        val glyph = contentRepository.glyphOf(letter)
        timerJob?.cancel()
        _state.update {
            it.copy(
                letter = letter,
                phoneme = contentRepository.phonemeOf(letter),
                waypoints = glyph.waypoints,
                stroke = emptyList(),
                reached = 0,
                target = ceil(glyph.waypoints.size * COMPLETION_SHARE).toInt().coerceAtLeast(1),
                hinted = false,
                phase = ActivityPhase.LISTENING,
                cueTick = it.cueTick + 1,
                hapticsEnabled = settingsRepository.settings.value.soundCues
            )
        }
        armTimers()
    }

    private fun armTimers() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(GameConfig.HINT_DELAY_MS)
            if (settingsRepository.settings.value.autoHints) {
                _state.update { it.copy(hinted = true, phase = ActivityPhase.HINTING) }
            }
            delay(GameConfig.AUTO_ASSIST_MS - GameConfig.HINT_DELAY_MS)
            guideToEnd()
        }
    }

    private fun guideToEnd() {
        val current = _state.value
        _state.update {
            it.copy(
                stroke = current.waypoints,
                reached = current.waypoints.size
            )
        }
        celebrate()
    }

    private fun celebrate() {
        timerJob?.cancel()
        _state.update { it.copy(phase = ActivityPhase.CELEBRATING) }
        timerJob = viewModelScope.launch {
            delay(GameConfig.CELEBRATION_MS)
            finishGlyph()
        }
    }

    private fun finishGlyph() {
        val letter = _state.value.letter
        progressRepository.recordLetterHit(letter)
        val progress = progressRepository.progress.value
        val word = blendWordUseCase.wordStartingWith(letter, progress)
        progressRepository.recordWordRead(word.id)
        val sticker = awardStickerUseCase(progress)
        if (sticker) {
            progressRepository.awardSticker()
        }
        progressRepository.setLastActivity(ActivityKind.TRACE)
        _state.update { it.copy(phase = ActivityPhase.FINISHED) }
        eventChannel.trySend(TraceEvent.Finished(word.id, sticker))
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val COMPLETION_SHARE = 0.8f
    }
}
