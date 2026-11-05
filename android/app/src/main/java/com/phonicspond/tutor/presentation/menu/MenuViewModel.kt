package com.phonicspond.tutor.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonicspond.tutor.domain.repository.ProgressRepository
import com.phonicspond.tutor.domain.usecase.NextActivityUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val nextActivityUseCase: NextActivityUseCase
) : ViewModel() {

    val state: StateFlow<MenuUiState> = progressRepository.progress
        .map { progress ->
            MenuUiState(
                stickers = progress.stickers,
                masteredLetters = progress.masteredLetters.size,
                wordsRead = progress.wordsRead.size,
                nextActivity = nextActivityUseCase(progress.lastActivity)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MenuUiState()
        )
}
