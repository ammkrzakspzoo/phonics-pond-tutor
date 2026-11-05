package com.phonicspond.tutor.presentation.game

import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.domain.model.ActivityPhase
import com.phonicspond.tutor.domain.model.LetterCard

data class GameUiState(
    val itemIndex: Int = 0,
    val itemCount: Int = GameConfig.ROUND_ITEMS,
    val phoneme: String = "",
    val options: List<LetterCard> = emptyList(),
    val correctLetter: Char = ' ',
    val solvedLetter: Char? = null,
    val hinted: Boolean = false,
    val phase: ActivityPhase = ActivityPhase.IDLE,
    val cueTick: Int = 0,
    val hapticsEnabled: Boolean = true
)

sealed interface GameEvent {
    data class Finished(val wordId: String, val stickerEarned: Boolean) : GameEvent
}
