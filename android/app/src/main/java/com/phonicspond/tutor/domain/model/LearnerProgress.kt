package com.phonicspond.tutor.domain.model

data class LearnerProgress(
    val masteredLetters: Set<Char> = emptySet(),
    val letterHits: Map<Char, Int> = emptyMap(),
    val wordsRead: Set<String> = emptySet(),
    val stickers: Int = 0,
    val lastActivity: ActivityKind = ActivityKind.TRACE
)
