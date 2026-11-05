package com.phonicspond.tutor.core.di

import android.content.Context
import com.phonicspond.tutor.data.local.PondPreferences
import com.phonicspond.tutor.data.repository.ContentRepositoryImpl
import com.phonicspond.tutor.data.repository.ProgressRepositoryImpl
import com.phonicspond.tutor.data.repository.SettingsRepositoryImpl
import com.phonicspond.tutor.domain.repository.ContentRepository
import com.phonicspond.tutor.domain.repository.ProgressRepository
import com.phonicspond.tutor.domain.repository.SettingsRepository
import com.phonicspond.tutor.domain.usecase.AwardStickerUseCase
import com.phonicspond.tutor.domain.usecase.BlendWordUseCase
import com.phonicspond.tutor.domain.usecase.BuildSoundRoundUseCase
import com.phonicspond.tutor.domain.usecase.CheckAnswerUseCase
import com.phonicspond.tutor.domain.usecase.NextActivityUseCase
import com.phonicspond.tutor.domain.usecase.TraceProgressUseCase

object ServiceLocator {

    private var appContext: Context? = null

    fun init(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
        }
    }

    private val preferences: PondPreferences by lazy {
        PondPreferences(requireNotNull(appContext))
    }

    val contentRepository: ContentRepository by lazy { ContentRepositoryImpl() }

    val progressRepository: ProgressRepository by lazy { ProgressRepositoryImpl(preferences) }

    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(preferences) }

    val awardStickerUseCase: AwardStickerUseCase by lazy { AwardStickerUseCase() }

    val blendWordUseCase: BlendWordUseCase by lazy { BlendWordUseCase(contentRepository) }

    val buildSoundRoundUseCase: BuildSoundRoundUseCase by lazy {
        BuildSoundRoundUseCase(contentRepository)
    }

    val checkAnswerUseCase: CheckAnswerUseCase by lazy { CheckAnswerUseCase() }

    val nextActivityUseCase: NextActivityUseCase by lazy { NextActivityUseCase() }

    val traceProgressUseCase: TraceProgressUseCase by lazy { TraceProgressUseCase() }
}
