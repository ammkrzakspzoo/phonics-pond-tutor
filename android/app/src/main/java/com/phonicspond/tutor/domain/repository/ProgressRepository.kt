package com.phonicspond.tutor.domain.repository

import com.phonicspond.tutor.domain.model.ActivityKind
import com.phonicspond.tutor.domain.model.LearnerProgress
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {

    val progress: StateFlow<LearnerProgress>

    fun recordLetterHit(letter: Char)

    fun recordWordRead(wordId: String)

    fun awardSticker()

    fun setLastActivity(kind: ActivityKind)

    fun reset()
}
