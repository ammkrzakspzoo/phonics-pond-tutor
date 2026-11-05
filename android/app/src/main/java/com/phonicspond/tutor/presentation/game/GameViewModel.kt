package com.phonicspond.tutor.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.domain.model.ActivityKind
import com.phonicspond.tutor.domain.model.ActivityPhase
import com.phonicspond.tutor.domain.model.LetterCard
import com.phonicspond.tutor.domain.model.SoundHopItem
import com.phonicspond.tutor.domain.repository.ProgressRepository
import com.phonicspond.tutor.domain.repository.SettingsRepository
import com.phonicspond.tutor.domain.usecase.AwardStickerUseCase
import com.phonicspond.tutor.domain.usecase.BlendWordUseCase
import com.phonicspond.tutor.domain.usecase.BuildSoundRoundUseCase
import com.phonicspond.tutor.domain.usecase.CheckAnswerUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val buildSoundRoundUseCase: BuildSoundRoundUseCase,
    private val checkAnswerUseCase: CheckAnswerUseCase,
    private val blendWordUseCase: BlendWordUseCase,
    private val awardStickerUseCase: AwardStickerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val eventChannel = Channel<GameEvent>(Channel.BUFFERED)
    val events = eventChannel.receiveAsFlow()

    private var items: List<SoundHopItem> = emptyList()
    private var timerJob: Job? = null
    private var unaided = true

    fun onEnter() {
        if (items.isEmpty() || _state.value.phase == ActivityPhase.FINISHED) {
            startRound()
        } else {
            armTimers()
        }
    }

    fun onLeave() {
        timerJob?.cancel()
        timerJob = null
    }

    fun onPadTap(letter: Char) {
        val current = _state.value
        if (current.phase == ActivityPhase.CELEBRATING || current.phase == ActivityPhase.FINISHED) return
        val target = LetterCard(current.correctLetter, current.phoneme)
        if (checkAnswerUseCase(target, letter)) {
            solve(credit = unaided)
            return
        }
        unaided = false
        _state.update {
            it.copy(
                phase = ActivityPhase.HINTING,
                hinted = true,
                cueTick = it.cueTick + 1
            )
        }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(GameConfig.MISS_ASSIST_MS)
            solve(credit = false)
        }
    }

    fun onReplayCue() {
        _state.update { it.copy(cueTick = it.cueTick + 1, phase = ActivityPhase.LISTENING) }
    }

    fun onHintRequested() {
        unaided = false
        _state.update { it.copy(hinted = true, phase = ActivityPhase.HINTING) }
    }

    private fun startRound() {
        items = buildSoundRoundUseCase(progressRepository.progress.value)
        present(0)
    }

    private fun present(index: Int) {
        val item = items[index]
        unaided = true
        _state.update {
            it.copy(
                itemIndex = index,
                itemCount = items.size,
                phoneme = item.target.phoneme,
                options = item.options,
                correctLetter = item.target.letter,
                solvedLetter = null,
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
                unaided = false
                _state.update { it.copy(hinted = true, phase = ActivityPhase.HINTING) }
            }
            delay(GameConfig.AUTO_ASSIST_MS - GameConfig.HINT_DELAY_MS)
            solve(credit = false)
        }
    }

    private fun solve(credit: Boolean) {
        timerJob?.cancel()
        val letter = _state.value.correctLetter
        if (credit) {
            progressRepository.recordLetterHit(letter)
        }
        _state.update {
            it.copy(
                solvedLetter = letter,
                phase = ActivityPhase.CELEBRATING
            )
        }
        timerJob = viewModelScope.launch {
            delay(GameConfig.CELEBRATION_MS)
            advance()
        }
    }

    private fun advance() {
        val next = _state.value.itemIndex + 1
        if (next < items.size) {
            present(next)
        } else {
            finishRound()
        }
    }

    private fun finishRound() {
        val progress = progressRepository.progress.value
        val word = blendWordUseCase.wordStartingWith(_state.value.correctLetter, progress)
        progressRepository.recordWordRead(word.id)
        val sticker = awardStickerUseCase(progress)
        if (sticker) {
            progressRepository.awardSticker()
        }
        progressRepository.setLastActivity(ActivityKind.SOUND_HOP)
        _state.update { it.copy(phase = ActivityPhase.FINISHED) }
        eventChannel.trySend(GameEvent.Finished(word.id, sticker))
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
