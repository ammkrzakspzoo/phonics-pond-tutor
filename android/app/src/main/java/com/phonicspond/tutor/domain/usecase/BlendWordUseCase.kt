package com.phonicspond.tutor.domain.usecase

import com.phonicspond.tutor.domain.model.LearnerProgress
import com.phonicspond.tutor.domain.model.PhonicsPlan
import com.phonicspond.tutor.domain.model.PhonicsWord
import com.phonicspond.tutor.domain.repository.ContentRepository

class BlendWordUseCase(private val content: ContentRepository) {

    operator fun invoke(progress: LearnerProgress): PhonicsWord {
        val words = content.words()
        val group = PhonicsPlan.activeGroup(progress.masteredLetters).toSet()
        val fitting = words.filter { word -> word.letters.all { it in group } }
        val source = if (fitting.isNotEmpty()) fitting else words
        return source.firstOrNull { it.id !in progress.wordsRead } ?: source.random()
    }

    fun wordStartingWith(letter: Char, progress: LearnerProgress): PhonicsWord {
        val words = content.words()
        val starting = words.filter { it.text.firstOrNull() == letter }
        val source = if (starting.isNotEmpty()) starting else words
        return source.firstOrNull { it.id !in progress.wordsRead } ?: source.random()
    }
}
