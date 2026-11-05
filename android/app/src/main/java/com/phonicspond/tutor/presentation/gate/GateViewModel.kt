package com.phonicspond.tutor.presentation.gate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonicspond.tutor.domain.model.PhonicsPlan
import com.phonicspond.tutor.domain.repository.ProgressRepository
import com.phonicspond.tutor.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GateViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        GateUiState(
            options = GATE_OPTIONS.shuffled(),
            letters = PhonicsPlan.order
        )
    )
    val state: StateFlow<GateUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                progressRepository.progress,
                settingsRepository.settings
            ) { progress, settings ->
                progress to settings
            }.collect { (progress, settings) ->
                _state.update {
                    it.copy(
                        masteredLetters = progress.masteredLetters.size,
                        wordsRead = progress.wordsRead.size,
                        stickers = progress.stickers,
                        masteredSet = progress.masteredLetters,
                        soundCues = settings.soundCues,
                        autoHints = settings.autoHints
                    )
                }
            }
        }
    }

    fun onAnswer(value: Int) {
        if (value == GATE_ANSWER) {
            _state.update { it.copy(unlocked = true, showRetry = false) }
        } else {
            _state.update {
                it.copy(
                    showRetry = true,
                    options = GATE_OPTIONS.shuffled()
                )
            }
        }
    }

    fun onToggleSoundCues(enabled: Boolean) {
        settingsRepository.setSoundCues(enabled)
    }

    fun onToggleAutoHints(enabled: Boolean) {
        settingsRepository.setAutoHints(enabled)
    }

    fun onResetRequested() {
        _state.update { it.copy(confirmReset = true) }
    }

    fun onResetCancelled() {
        _state.update { it.copy(confirmReset = false) }
    }

    fun onResetConfirmed() {
        progressRepository.reset()
        _state.update { it.copy(confirmReset = false) }
    }

    private companion object {
        const val GATE_ANSWER = 13
        val GATE_OPTIONS = listOf(11, 13, 16)
    }
}
