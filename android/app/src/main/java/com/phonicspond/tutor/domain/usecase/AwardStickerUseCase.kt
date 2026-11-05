package com.phonicspond.tutor.domain.usecase

import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.domain.model.LearnerProgress

class AwardStickerUseCase {

    operator fun invoke(progress: LearnerProgress): Boolean =
        progress.stickers < GameConfig.STICKER_SLOTS
}
