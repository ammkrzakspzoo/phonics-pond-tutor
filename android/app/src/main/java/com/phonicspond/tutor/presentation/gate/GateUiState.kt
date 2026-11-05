package com.phonicspond.tutor.presentation.gate

data class GateUiState(
    val unlocked: Boolean = false,
    val options: List<Int> = emptyList(),
    val showRetry: Boolean = false,
    val masteredLetters: Int = 0,
    val wordsRead: Int = 0,
    val stickers: Int = 0,
    val letters: List<Char> = emptyList(),
    val masteredSet: Set<Char> = emptySet(),
    val soundCues: Boolean = true,
    val autoHints: Boolean = true,
    val confirmReset: Boolean = false
)
