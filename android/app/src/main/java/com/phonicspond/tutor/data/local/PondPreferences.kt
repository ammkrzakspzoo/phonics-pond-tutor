package com.phonicspond.tutor.data.local

import android.content.Context
import android.content.SharedPreferences

class PondPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var masteredLetters: Set<Char>
        get() = decodeChars(prefs.getString(KEY_MASTERED, "").orEmpty())
        set(value) = prefs.edit().putString(KEY_MASTERED, value.joinToString(",")).apply()

    var wordsRead: Set<String>
        get() = prefs.getString(KEY_WORDS, "").orEmpty()
            .split(",")
            .filter { it.isNotBlank() }
            .toSet()
        set(value) = prefs.edit().putString(KEY_WORDS, value.joinToString(",")).apply()

    var stickers: Int
        get() = prefs.getInt(KEY_STICKERS, 0)
        set(value) = prefs.edit().putInt(KEY_STICKERS, value).apply()

    var lastActivity: String
        get() = prefs.getString(KEY_LAST_ACTIVITY, "").orEmpty()
        set(value) = prefs.edit().putString(KEY_LAST_ACTIVITY, value).apply()

    var soundCues: Boolean
        get() = prefs.getBoolean(KEY_SOUND_CUES, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_CUES, value).apply()

    var autoHints: Boolean
        get() = prefs.getBoolean(KEY_AUTO_HINTS, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_HINTS, value).apply()

    fun letterHits(letters: List<Char>): Map<Char, Int> =
        letters.associateWith { prefs.getInt(KEY_HITS_PREFIX + it, 0) }
            .filterValues { it > 0 }

    fun hitsFor(letter: Char): Int = prefs.getInt(KEY_HITS_PREFIX + letter, 0)

    fun setHits(letter: Char, hits: Int) {
        prefs.edit().putInt(KEY_HITS_PREFIX + letter, hits).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private fun decodeChars(raw: String): Set<Char> =
        raw.split(",").mapNotNull { it.trim().firstOrNull() }.toSet()

    private companion object {
        const val FILE_NAME = "phonics_pond_prefs"
        const val KEY_MASTERED = "mastered_letters"
        const val KEY_WORDS = "words_read"
        const val KEY_STICKERS = "stickers_earned"
        const val KEY_LAST_ACTIVITY = "last_activity"
        const val KEY_SOUND_CUES = "setting_sound_cues"
        const val KEY_AUTO_HINTS = "setting_auto_hints"
        const val KEY_HITS_PREFIX = "letter_hits_"
    }
}
