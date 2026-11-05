package com.phonicspond.tutor.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.phonicspond.tutor.R
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.LilyPink
import com.phonicspond.tutor.core.theme.SurfaceWhite

@Composable
fun PondBackground(
    overlay: List<Color>,
    modifier: Modifier = Modifier,
    blobTint: Color = LilyPink,
    blobStrength: Float = 0.12f,
    rippleTint: Color = FrogGreen,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val topBlob = Offset(size.width * 0.14f, size.height * 0.10f)
            val topRadius = size.minDimension * 0.52f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(SurfaceWhite.copy(alpha = 0.34f), Color.Transparent),
                    center = topBlob,
                    radius = topRadius
                ),
                radius = topRadius,
                center = topBlob
            )
            val bottomBlob = Offset(size.width * 0.90f, size.height * 0.86f)
            val bottomRadius = size.minDimension * 0.62f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobTint.copy(alpha = blobStrength), Color.Transparent),
                    center = bottomBlob,
                    radius = bottomRadius
                ),
                radius = bottomRadius,
                center = bottomBlob
            )
            val lily = rippleTint.copy(alpha = 0.07f)
            drawCircle(
                color = lily,
                radius = size.minDimension * 0.11f,
                center = Offset(size.width * 0.84f, size.height * 0.20f)
            )
            drawCircle(
                color = lily,
                radius = size.minDimension * 0.08f,
                center = Offset(size.width * 0.10f, size.height * 0.62f)
            )
            drawCircle(
                color = lily,
                radius = size.minDimension * 0.13f,
                center = Offset(size.width * 0.72f, size.height * 0.94f)
            )
            drawCircle(
                color = lily,
                radius = size.minDimension * 0.06f,
                center = Offset(size.width * 0.30f, size.height * 0.33f)
            )
            val ringStroke = Stroke(width = 2.dp.toPx())
            val ringCenter = Offset(size.width * 0.5f, size.height * 0.58f)
            drawCircle(
                color = rippleTint.copy(alpha = 0.10f),
                radius = size.minDimension * 0.34f,
                center = ringCenter,
                style = ringStroke
            )
            drawCircle(
                color = rippleTint.copy(alpha = 0.08f),
                radius = size.minDimension * 0.46f,
                center = ringCenter,
                style = ringStroke
            )
            drawCircle(
                color = rippleTint.copy(alpha = 0.06f),
                radius = size.minDimension * 0.58f,
                center = ringCenter,
                style = ringStroke
            )
        }
        content()
    }
}
