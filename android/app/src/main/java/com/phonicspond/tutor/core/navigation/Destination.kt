package com.phonicspond.tutor.core.navigation

import com.phonicspond.tutor.domain.model.ActivityKind

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data object Game : Screen

    data object Blend : Screen

    data object Trace : Screen

    data object Stickers : Screen

    data object GrownUp : Screen

    data class GameOver(
        val wordId: String,
        val activity: ActivityKind,
        val stickerEarned: Boolean
    ) : Screen
}
