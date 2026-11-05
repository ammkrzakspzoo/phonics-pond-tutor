package com.phonicspond.tutor.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phonicspond.tutor.core.theme.FrogGreen
import com.phonicspond.tutor.core.theme.SurfaceWhite
import com.phonicspond.tutor.core.theme.TextPrimary

@Composable
fun FloatingPillBar(
    firstLabel: String,
    firstIcon: ImageVector,
    onFirst: () -> Unit,
    secondLabel: String,
    secondIcon: ImageVector,
    onSecond: () -> Unit,
    modifier: Modifier = Modifier
) {
    ClayCard(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp),
        fill = listOf(SurfaceWhite.copy(alpha = 0.94f), SurfaceWhite.copy(alpha = 0.86f)),
        corner = 34.dp,
        elevation = 12.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PillAction(label = firstLabel, icon = firstIcon, onClick = onFirst)
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .background(FrogGreen.copy(alpha = 0.25f))
            )
            PillAction(label = secondLabel, icon = secondIcon, onClick = onSecond)
        }
    }
}

@Composable
private fun PillAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FrogGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            lineHeight = 24.sp
        )
    }
}
