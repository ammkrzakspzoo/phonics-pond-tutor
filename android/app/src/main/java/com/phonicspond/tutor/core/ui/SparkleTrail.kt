package com.phonicspond.tutor.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.RewardGold
import com.phonicspond.tutor.core.theme.SurfaceWhite

@Composable
fun SparkleTrail(
    points: List<Offset>,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 6f
) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val path = Path()
        path.moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { path.lineTo(it.x, it.y) }
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(LilyPink, RewardGold),
                start = points.first(),
                end = points.last()
            ),
            style = Stroke(
                width = strokeWidth.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        points.takeLast(10).forEachIndexed { index, point ->
            drawCircle(
                color = SurfaceWhite.copy(alpha = 0.25f + index * 0.06f),
                radius = (3f + index * 0.25f).dp.toPx(),
                center = point
            )
        }
    }
}
