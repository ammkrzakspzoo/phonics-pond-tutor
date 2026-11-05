package com.phonicspond.tutor.presentation.menu

import com.phonicspond.tutor.domain.model.ActivityKind

data class MenuUiState(
    val stickers: Int = 0,
    val masteredLetters: Int = 0,
    val wordsRead: Int = 0,
    val nextActivity: ActivityKind = ActivityKind.SOUND_HOP
)
