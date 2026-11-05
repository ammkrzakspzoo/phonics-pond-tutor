package com.phonicspond.tutor.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.phonicspond.tutor.core.theme.FrogDark
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.SurfaceWhite

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    fill: List<Color> = listOf(SurfaceWhite.copy(alpha = 0.96f), SurfaceWhite.copy(alpha = 0.86f)),
    corner: Dp = 28.dp,
    elevation: Dp = 12.dp,
    shadowTint: Color = FrogDark,
    borderTint: Color = FrogGreen.copy(alpha = 0.20f),
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = shadowTint.copy(alpha = 0.40f),
                spotColor = shadowTint.copy(alpha = 0.40f)
            )
            .clip(shape)
            .background(Brush.verticalGradient(fill))
            .border(width = 1.5.dp, color = borderTint, shape = shape),
        contentAlignment = contentAlignment
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(2.dp)
                .background(SurfaceWhite.copy(alpha = 0.30f))
        )
        content()
    }
}
