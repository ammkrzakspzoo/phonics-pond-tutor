package com.phonicspond.tutor.domain.repository

import com.phonicspond.tutor.domain.model.PhonicsWord
import com.phonicspond.tutor.domain.model.TraceGlyph

interface ContentRepository {

    fun words(): List<PhonicsWord>

    fun phonemeOf(letter: Char): String

    fun glyphOf(letter: Char): TraceGlyph

    fun wordById(id: String): PhonicsWord?
}
