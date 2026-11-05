package com.phonicspond.tutor.presentation.gameover

import com.phonicspond.tutor.domain.model.ActivityKind

data class GameOverUiState(
    val word: String = "",
    val phonemes: List<String> = emptyList(),
    val stickerEarned: Boolean = false,
    val wordsRead: Int = 0,
    val stickers: Int = 0,
    val masteredLetters: Int = 0,
    val spriteIndex: Int = 0,
    val nextActivity: ActivityKind = ActivityKind.SOUND_HOP
)
