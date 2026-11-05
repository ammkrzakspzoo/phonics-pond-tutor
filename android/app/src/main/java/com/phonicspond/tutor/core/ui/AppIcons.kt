package com.phonicspond.tutor.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.phonicspond.tutor.R

object AppIcons {
    val Check: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_check)

    val Clear: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_clear)

    val Info: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_info)

    val Lock: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_lock)

    val PlayArrow: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_play_arrow)

    val Refresh: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_refresh)

    val Star: ImageVector
        @Composable get() = ImageVector.vectorResource(R.drawable.ic_star)
}
