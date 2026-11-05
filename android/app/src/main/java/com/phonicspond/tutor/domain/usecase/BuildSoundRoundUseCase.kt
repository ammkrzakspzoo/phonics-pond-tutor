package com.phonicspond.tutor.domain.usecase

import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.domain.model.LearnerProgress
import com.phonicspond.tutor.domain.model.LetterCard
import com.phonicspond.tutor.domain.model.PhonicsPlan
import com.phonicspond.tutor.domain.model.SoundHopItem
import com.phonicspond.tutor.domain.repository.ContentRepository

class BuildSoundRoundUseCase(private val content: ContentRepository) {

    operator fun invoke(progress: LearnerProgress): List<SoundHopItem> {
        val group = PhonicsPlan.activeGroup(progress.masteredLetters)
        val pool = if (group.size >= GameConfig.SOUND_HOP_OPTIONS) {
            group
        } else {
            (group + PhonicsPlan.order).distinct()
        }
        val targets = group.sortedBy { progress.letterHits[it] ?: 0 }
        return (0 until GameConfig.ROUND_ITEMS).map { index ->
            val letter = targets[index % targets.size]
            val distractors = pool
                .filter { it != letter }
                .shuffled()
                .take(GameConfig.SOUND_HOP_OPTIONS - 1)
            val options = (distractors + letter).shuffled().map { card(it) }
            SoundHopItem(target = card(letter), options = options)
        }
    }

    private fun card(letter: Char) = LetterCard(letter, content.phonemeOf(letter))
}
