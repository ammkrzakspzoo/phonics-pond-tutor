package com.phonicspond.tutor.presentation.blend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.domain.model.ActivityKind
import com.phonicspond.tutor.domain.model.ActivityPhase
import com.phonicspond.tutor.domain.repository.ProgressRepository
import com.phonicspond.tutor.domain.repository.SettingsRepository
import com.phonicspond.tutor.domain.usecase.AwardStickerUseCase
import com.phonicspond.tutor.domain.usecase.BlendWordUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlendViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val blendWordUseCase: BlendWordUseCase,
    private val awardStickerUseCase: AwardStickerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BlendUiState())
    val state: StateFlow<BlendUiState> = _state.asStateFlow()

    private val eventChannel = Channel<BlendEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var timerJob: Job? = null

    fun onEnter() {
        if (_state.value.letters.isEmpty() || _state.value.phase == ActivityPhase.FINISHED) {
            startRound()
        } else {
            armTimers()
        }
    }

    fun onLeave() {
        timerJob?.cancel()
        timerJob = null
    }

    fun onPadCrossed(index: Int) {
        val current = _state.value
        if (current.phase == ActivityPhase.CELEBRATING || current.phase == ActivityPhase.FINISHED) return
        if (index != current.crossed) return
        advance()
    }

    fun onReplayCue() {
        _state.update { it.copy(cueTick = it.cueTick + 1, phase = ActivityPhase.LISTENING) }
    }

    fun onHintRequested() {
        _state.update { it.copy(hinted = true, phase = ActivityPhase.HINTING) }
    }

    private fun startRound() {
        val word = blendWordUseCase(progressRepository.progress.value)
        timerJob?.cancel()
        _state.update {
            it.copy(
                wordId = word.id,
                word = word.text,
                letters = word.letters,
                phonemes = word.phonemes,
                crossed = 0,
                hinted = false,
                phase = ActivityPhase.IDLE,
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
            repeat(_state.value.letters.size - _state.value.crossed) {
                advance()
                if (_state.value.phase == ActivityPhase.CELEBRATING) return@launch
                delay(GameConfig.PAD_RIPPLE_MS.toLong())
            }
        }
    }

    private fun advance() {
        val current = _state.value
        val crossed = current.crossed + 1
        _state.update {
            it.copy(
                crossed = crossed,
                phase = if (crossed >= current.letters.size) {
                    ActivityPhase.CELEBRATING
                } else {
                    ActivityPhase.ACTIVE
                },
                cueTick = it.cueTick + 1
            )
        }
        if (crossed >= current.letters.size) {
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                delay(GameConfig.CELEBRATION_MS)
                finishRound()
            }
        }
    }

    private fun finishRound() {
        val progress = progressRepository.progress.value
        val current = _state.value
        progressRepository.recordWordRead(current.wordId)
        val sticker = awardStickerUseCase(progress)
        if (sticker) {
            progressRepository.awardSticker()
        }
        progressRepository.setLastActivity(ActivityKind.BLEND)
        _state.update { it.copy(phase = ActivityPhase.FINISHED) }
        eventChannel.trySend(BlendEvent.Finished(current.wordId, sticker))
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
