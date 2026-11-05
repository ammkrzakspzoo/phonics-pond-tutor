package com.phonicspond.tutor.presentation.stickers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StickerViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val state: StateFlow<StickerUiState> = progressRepository.progress
        .map { progress ->
            StickerUiState(
                collected = progress.stickers,
                total = GameConfig.STICKER_SLOTS,
                wordsRead = progress.wordsRead.size,
                masteredLetters = progress.masteredLetters.size
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StickerUiState(total = GameConfig.STICKER_SLOTS)
        )
}
