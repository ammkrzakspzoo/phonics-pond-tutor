package com.phonicspond.tutor.domain.usecase

import com.phonicspond.tutor.domain.model.TracePoint
import kotlin.math.hypot

class TraceProgressUseCase {

    operator fun invoke(
        waypoints: List<TracePoint>,
        consumed: Int,
        point: TracePoint,
        radius: Float
    ): Int {
        if (consumed >= waypoints.size) return consumed
        var reached = consumed
        while (reached < waypoints.size) {
            val next = waypoints[reached]
            val distance = hypot(next.x - point.x, next.y - point.y)
            if (distance > radius) break
            reached++
        }
        return reached
    }
}
