package com.phonicspond.tutor.domain.usecase

import com.phonicspond.tutor.domain.model.LetterCard

class CheckAnswerUseCase {

    operator fun invoke(target: LetterCard, chosen: Char): Boolean = target.letter == chosen
}
