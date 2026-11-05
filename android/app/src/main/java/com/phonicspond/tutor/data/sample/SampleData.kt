package com.phonicspond.tutor.data.sample

import com.phonicspond.tutor.domain.model.PhonicsWord
import com.phonicspond.tutor.domain.model.TraceGlyph
import com.phonicspond.tutor.domain.model.TracePoint

object SampleData {

    private val phonemeOverrides = mapOf(
        'c' to "k",
        'q' to "kw",
        'x' to "ks"
    )

    private val cvcWords = listOf(
        "sat", "pat", "tap", "nap", "pin", "sip", "tip", "man", "mad", "dad",
        "dig", "dog", "cot", "cat", "cap", "kid", "kit", "hen", "pen", "red",
        "bed", "bug", "sun", "run", "hug", "fun", "jam", "van", "wet", "yes",
        "zip", "box", "fox", "leg", "log", "mop", "top", "hop", "bun", "cup",
        "nut", "net", "jet", "lid", "lip", "rug", "mug", "hat", "ham", "bag"
    )

    val words: List<PhonicsWord> = cvcWords.map { text ->
        PhonicsWord(
            id = text,
            text = text,
            phonemes = text.map { phoneme(it) }
        )
    }

    fun phoneme(letter: Char): String =
        phonemeOverrides[letter.lowercaseChar()] ?: letter.lowercaseChar().toString()

    private val fallbackPath = listOf(
        TracePoint(0.50f, 0.20f),
        TracePoint(0.50f, 0.38f),
        TracePoint(0.50f, 0.56f),
        TracePoint(0.50f, 0.72f),
        TracePoint(0.60f, 0.82f),
        TracePoint(0.72f, 0.78f)
    )

    private val paths: Map<Char, List<TracePoint>> = mapOf(
        's' to points(0.70f, 0.28f, 0.45f, 0.20f, 0.30f, 0.33f, 0.45f, 0.47f, 0.62f, 0.58f, 0.55f, 0.76f, 0.32f, 0.76f),
        'a' to points(0.66f, 0.34f, 0.46f, 0.26f, 0.32f, 0.44f, 0.40f, 0.66f, 0.60f, 0.68f, 0.66f, 0.50f, 0.68f, 0.76f),
        't' to points(0.52f, 0.16f, 0.52f, 0.38f, 0.52f, 0.60f, 0.56f, 0.76f, 0.68f, 0.80f, 0.34f, 0.40f, 0.70f, 0.40f),
        'p' to points(0.32f, 0.28f, 0.32f, 0.50f, 0.32f, 0.70f, 0.32f, 0.88f, 0.50f, 0.30f, 0.64f, 0.42f, 0.52f, 0.58f, 0.34f, 0.58f),
        'i' to points(0.50f, 0.32f, 0.50f, 0.48f, 0.50f, 0.64f, 0.50f, 0.78f, 0.50f, 0.18f),
        'n' to points(0.34f, 0.72f, 0.34f, 0.52f, 0.34f, 0.36f, 0.48f, 0.30f, 0.62f, 0.38f, 0.64f, 0.54f, 0.64f, 0.72f),
        'm' to points(0.26f, 0.72f, 0.26f, 0.50f, 0.26f, 0.34f, 0.40f, 0.32f, 0.50f, 0.46f, 0.50f, 0.70f, 0.60f, 0.34f, 0.72f, 0.44f, 0.72f, 0.70f),
        'd' to points(0.64f, 0.18f, 0.64f, 0.44f, 0.64f, 0.68f, 0.52f, 0.74f, 0.36f, 0.64f, 0.36f, 0.44f, 0.52f, 0.32f, 0.64f, 0.36f),
        'g' to points(0.64f, 0.34f, 0.48f, 0.26f, 0.34f, 0.42f, 0.42f, 0.62f, 0.60f, 0.58f, 0.64f, 0.44f, 0.64f, 0.74f, 0.52f, 0.88f, 0.36f, 0.82f),
        'o' to points(0.50f, 0.26f, 0.34f, 0.38f, 0.32f, 0.56f, 0.46f, 0.72f, 0.62f, 0.64f, 0.66f, 0.44f, 0.52f, 0.28f),
        'c' to points(0.66f, 0.34f, 0.50f, 0.26f, 0.34f, 0.40f, 0.34f, 0.60f, 0.50f, 0.74f, 0.66f, 0.68f),
        'k' to points(0.34f, 0.18f, 0.34f, 0.42f, 0.34f, 0.64f, 0.34f, 0.82f, 0.64f, 0.42f, 0.44f, 0.60f, 0.68f, 0.82f),
        'e' to points(0.34f, 0.54f, 0.52f, 0.52f, 0.66f, 0.50f, 0.60f, 0.34f, 0.44f, 0.30f, 0.34f, 0.48f, 0.38f, 0.66f, 0.54f, 0.74f, 0.68f, 0.66f),
        'u' to points(0.34f, 0.30f, 0.34f, 0.50f, 0.36f, 0.66f, 0.50f, 0.74f, 0.62f, 0.64f, 0.64f, 0.46f, 0.64f, 0.30f, 0.64f, 0.76f),
        'r' to points(0.38f, 0.74f, 0.38f, 0.54f, 0.38f, 0.34f, 0.50f, 0.28f, 0.64f, 0.32f),
        'h' to points(0.34f, 0.18f, 0.34f, 0.40f, 0.34f, 0.60f, 0.34f, 0.78f, 0.48f, 0.42f, 0.62f, 0.48f, 0.62f, 0.78f),
        'b' to points(0.34f, 0.18f, 0.34f, 0.40f, 0.34f, 0.62f, 0.34f, 0.80f, 0.50f, 0.80f, 0.64f, 0.68f, 0.58f, 0.50f, 0.40f, 0.48f),
        'f' to points(0.64f, 0.24f, 0.52f, 0.20f, 0.44f, 0.32f, 0.44f, 0.52f, 0.44f, 0.72f, 0.44f, 0.84f, 0.30f, 0.46f, 0.60f, 0.46f),
        'l' to points(0.50f, 0.18f, 0.50f, 0.38f, 0.50f, 0.56f, 0.50f, 0.72f, 0.58f, 0.82f, 0.70f, 0.80f),
        'j' to points(0.58f, 0.16f, 0.58f, 0.34f, 0.58f, 0.52f, 0.58f, 0.70f, 0.50f, 0.84f, 0.36f, 0.80f),
        'v' to points(0.32f, 0.30f, 0.40f, 0.50f, 0.50f, 0.74f, 0.60f, 0.50f, 0.68f, 0.30f),
        'w' to points(0.26f, 0.30f, 0.34f, 0.56f, 0.42f, 0.74f, 0.50f, 0.48f, 0.58f, 0.74f, 0.66f, 0.56f, 0.74f, 0.30f),
        'x' to points(0.32f, 0.30f, 0.44f, 0.48f, 0.56f, 0.62f, 0.68f, 0.78f, 0.68f, 0.30f, 0.56f, 0.48f, 0.32f, 0.78f),
        'y' to points(0.32f, 0.30f, 0.42f, 0.48f, 0.50f, 0.62f, 0.60f, 0.44f, 0.68f, 0.30f, 0.56f, 0.76f, 0.46f, 0.88f),
        'z' to points(0.32f, 0.32f, 0.50f, 0.32f, 0.68f, 0.32f, 0.50f, 0.56f, 0.34f, 0.76f, 0.52f, 0.76f, 0.70f, 0.76f),
        'q' to points(0.62f, 0.34f, 0.48f, 0.26f, 0.34f, 0.42f, 0.42f, 0.62f, 0.60f, 0.58f, 0.64f, 0.42f, 0.64f, 0.66f, 0.64f, 0.88f)
    )

    fun glyph(letter: Char): TraceGlyph {
        val key = letter.lowercaseChar()
        return TraceGlyph(key, paths[key] ?: fallbackPath)
    }

    private fun points(vararg values: Float): List<TracePoint> =
        values.toList().chunked(2).map { TracePoint(it[0], it[1]) }
}
