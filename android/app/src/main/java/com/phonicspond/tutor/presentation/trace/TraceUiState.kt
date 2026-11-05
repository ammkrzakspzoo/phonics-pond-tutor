package com.phonicspond.tutor.presentation.trace

import com.phonicspond.tutor.domain.model.ActivityPhase
import com.phonicspond.tutor.domain.model.TracePoint

data class TraceUiState(
    val letter: Char = ' ',
    val phoneme: String = "",
    val waypoints: List<TracePoint> = emptyList(),
    val stroke: List<TracePoint> = emptyList(),
    val reached: Int = 0,
    val target: Int = 0,
    val hinted: Boolean = false,
    val phase: ActivityPhase = ActivityPhase.IDLE,
    val cueTick: Int = 0,
    val hapticsEnabled: Boolean = true
)

sealed interface TraceEvent {
    data class Finished(val wordId: String, val stickerEarned: Boolean) : TraceEvent
}
