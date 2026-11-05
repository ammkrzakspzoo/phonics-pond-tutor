package com.phonicspond.tutor.data.repository

import com.phonicspond.tutor.core.config.GameConfig
import com.phonicspond.tutor.data.local.PondPreferences
import com.phonicspond.tutor.domain.model.ActivityKind
import com.phonicspond.tutor.domain.model.LearnerProgress
import com.phonicspond.tutor.domain.model.PhonicsPlan
import com.phonicspond.tutor.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressRepositoryImpl(private val prefs: PondPreferences) : ProgressRepository {

    private val state = MutableStateFlow(read())

    override val progress: StateFlow<LearnerProgress> = state.asStateFlow()

    override fun recordLetterHit(letter: Char) {
        val hits = prefs.hitsFor(letter) + 1
        prefs.setHits(letter, hits)
        if (hits >= GameConfig.MASTERY_HITS) {
            prefs.masteredLetters = prefs.masteredLetters + letter
        }
        state.value = read()
    }

    override fun recordWordRead(wordId: String) {
        prefs.wordsRead = prefs.wordsRead + wordId
        state.value = read()
    }

    override fun awardSticker() {
        prefs.stickers = (prefs.stickers + 1).coerceAtMost(GameConfig.STICKER_SLOTS)
        state.value = read()
    }

    override fun setLastActivity(kind: ActivityKind) {
        prefs.lastActivity = kind.name
        state.value = read()
    }

    override fun reset() {
        prefs.clear()
        state.value = read()
    }

    private fun read(): LearnerProgress = LearnerProgress(
        masteredLetters = prefs.masteredLetters,
        letterHits = prefs.letterHits(PhonicsPlan.order),
        wordsRead = prefs.wordsRead,
        stickers = prefs.stickers,
        lastActivity = parseActivity(prefs.lastActivity)
    )

    private fun parseActivity(raw: String): ActivityKind =
        ActivityKind.entries.firstOrNull { it.name == raw } ?: ActivityKind.TRACE
}
