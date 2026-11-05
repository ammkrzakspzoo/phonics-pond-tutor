package com.phonicspond.tutor.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.phonicspond.tutor.presentation.app.AppViewModel
import com.phonicspond.tutor.presentation.blend.BlendViewModel
import com.phonicspond.tutor.presentation.game.GameViewModel
import com.phonicspond.tutor.presentation.gameover.GameOverViewModel
import com.phonicspond.tutor.presentation.gate.GateViewModel
import com.phonicspond.tutor.presentation.menu.MenuViewModel
import com.phonicspond.tutor.presentation.splash.SplashViewModel
import com.phonicspond.tutor.presentation.stickers.StickerViewModel
import com.phonicspond.tutor.presentation.trace.TraceViewModel

object AppViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(
                progressRepository = ServiceLocator.progressRepository,
                nextActivityUseCase = ServiceLocator.nextActivityUseCase
            )

            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                progressRepository = ServiceLocator.progressRepository,
                settingsRepository = ServiceLocator.settingsRepository,
                buildSoundRoundUseCase = ServiceLocator.buildSoundRoundUseCase,
                checkAnswerUseCase = ServiceLocator.checkAnswerUseCase,
                blendWordUseCase = ServiceLocator.blendWordUseCase,
                awardStickerUseCase = ServiceLocator.awardStickerUseCase
            )

            modelClass.isAssignableFrom(BlendViewModel::class.java) -> BlendViewModel(
                progressRepository = ServiceLocator.progressRepository,
                settingsRepository = ServiceLocator.settingsRepository,
                blendWordUseCase = ServiceLocator.blendWordUseCase,
                awardStickerUseCase = ServiceLocator.awardStickerUseCase
            )

            modelClass.isAssignableFrom(TraceViewModel::class.java) -> TraceViewModel(
                progressRepository = ServiceLocator.progressRepository,
                settingsRepository = ServiceLocator.settingsRepository,
                contentRepository = ServiceLocator.contentRepository,
                traceProgressUseCase = ServiceLocator.traceProgressUseCase,
                blendWordUseCase = ServiceLocator.blendWordUseCase,
                awardStickerUseCase = ServiceLocator.awardStickerUseCase
            )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                progressRepository = ServiceLocator.progressRepository,
                contentRepository = ServiceLocator.contentRepository,
                nextActivityUseCase = ServiceLocator.nextActivityUseCase
            )

            modelClass.isAssignableFrom(StickerViewModel::class.java) -> StickerViewModel(
                progressRepository = ServiceLocator.progressRepository
            )

            modelClass.isAssignableFrom(GateViewModel::class.java) -> GateViewModel(
                progressRepository = ServiceLocator.progressRepository,
                settingsRepository = ServiceLocator.settingsRepository
            )

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
