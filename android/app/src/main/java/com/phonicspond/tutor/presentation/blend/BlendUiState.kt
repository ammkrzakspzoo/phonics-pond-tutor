package com.phonicspond.tutor.presentation.blend

import com.phonicspond.tutor.domain.model.ActivityPhase

data class BlendUiState(
    val wordId: String = "",
    val word: String = "",
    val letters: List<Char> = emptyList(),
    val phonemes: List<String> = emptyList(),
    val crossed: Int = 0,
    val hinted: Boolean = false,
    val phase: ActivityPhase = ActivityPhase.IDLE,
    val cueTick: Int = 0,
    val hapticsEnabled: Boolean = true
)

sealed interface BlendEvent {
    data class Finished(val wordId: String, val stickerEarned: Boolean) : BlendEvent
}
