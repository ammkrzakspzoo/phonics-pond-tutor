package com.phonicspond.tutor.domain.usecase

import com.phonicspond.tutor.domain.model.ActivityKind

class NextActivityUseCase {

    operator fun invoke(last: ActivityKind): ActivityKind = when (last) {
        ActivityKind.SOUND_HOP -> ActivityKind.BLEND
        ActivityKind.BLEND -> ActivityKind.TRACE
        ActivityKind.TRACE -> ActivityKind.SOUND_HOP
    }
}
