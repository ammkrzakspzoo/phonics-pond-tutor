package com.phonicspond.tutor.domain.model

data class TracePoint(
    val x: Float,
    val y: Float
)

data class TraceGlyph(
    val letter: Char,
    val waypoints: List<TracePoint>
)
