package com.phonicspond.tutor.data.repository

import com.phonicspond.tutor.data.sample.SampleData
import com.phonicspond.tutor.domain.model.PhonicsWord
import com.phonicspond.tutor.domain.model.TraceGlyph
import com.phonicspond.tutor.domain.repository.ContentRepository

class ContentRepositoryImpl : ContentRepository {

    override fun words(): List<PhonicsWord> = SampleData.words

    override fun phonemeOf(letter: Char): String = SampleData.phoneme(letter)

    override fun glyphOf(letter: Char): TraceGlyph = SampleData.glyph(letter)

    override fun wordById(id: String): PhonicsWord? = SampleData.words.firstOrNull { it.id == id }
}
