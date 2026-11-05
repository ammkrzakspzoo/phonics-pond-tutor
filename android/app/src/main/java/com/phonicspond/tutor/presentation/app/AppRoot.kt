package com.phonicspond.tutor.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.core.di.AppViewModelFactory
import com.phonicspond.tutor.core.navigation.Screen
import com.phonicspond.tutor.domain.model.ActivityKind
import com.phonicspond.tutor.presentation.blend.BlendScreen
import com.phonicspond.tutor.presentation.game.GameScreen
import com.phonicspond.tutor.presentation.gameover.GameOverScreen
import com.phonicspond.tutor.presentation.gate.GrownUpGateScreen
import com.phonicspond.tutor.presentation.menu.MenuScreen
import com.phonicspond.tutor.presentation.splash.SplashScreen
import com.phonicspond.tutor.presentation.stickers.StickerLilyScreen
import com.phonicspond.tutor.presentation.trace.LetterTraceScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) {
        appViewModel.back()
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            fadeIn(tween(GameConfig.SCREEN_FADE_MS)) togetherWith
                fadeOut(tween(GameConfig.SCREEN_FADE_MS))
        },
        label = "screenHost"
    ) { target ->
        when (target) {
            Screen.Splash -> SplashScreen(
                onFinished = appViewModel::showMenu,
                modifier = Modifier.fillMaxSize()
            )

            Screen.Menu -> MenuScreen(
                onPlay = appViewModel::startActivity,
                onBlend = { appViewModel.startActivity(ActivityKind.BLEND) },
                onTrace = { appViewModel.startActivity(ActivityKind.TRACE) },
                onStickers = { appViewModel.navigateTo(Screen.Stickers) },
                onGrownUp = { appViewModel.navigateTo(Screen.GrownUp) },
                modifier = Modifier.fillMaxSize()
            )

            Screen.Game -> GameScreen(
                onHome = appViewModel::showMenu,
                onFinished = { wordId, sticker ->
                    appViewModel.showResult(
                        Screen.GameOver(wordId, ActivityKind.SOUND_HOP, sticker)
                    )
                },
                modifier = Modifier.fillMaxSize()
            )

            Screen.Blend -> BlendScreen(
                onHome = appViewModel::showMenu,
                onFinished = { wordId, sticker ->
                    appViewModel.showResult(
                        Screen.GameOver(wordId, ActivityKind.BLEND, sticker)
                    )
                },
                modifier = Modifier.fillMaxSize()
            )

            Screen.Trace -> LetterTraceScreen(
                onHome = appViewModel::showMenu,
                onFinished = { wordId, sticker ->
                    appViewModel.showResult(
                        Screen.GameOver(wordId, ActivityKind.TRACE, sticker)
                    )
                },
                modifier = Modifier.fillMaxSize()
            )

            Screen.Stickers -> StickerLilyScreen(
                onHome = appViewModel::showMenu,
                modifier = Modifier.fillMaxSize()
            )

            Screen.GrownUp -> GrownUpGateScreen(
                onHome = appViewModel::showMenu,
                modifier = Modifier.fillMaxSize()
            )

            is Screen.GameOver -> GameOverScreen(
                route = target,
                onPlayAgain = appViewModel::startActivity,
                onMenu = appViewModel::showMenu,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
