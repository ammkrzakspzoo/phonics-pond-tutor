package com.phonicspond.tutor.domain.model

data class PhonicsWord(
    val id: String,
    val text: String,
    val phonemes: List<String>
) {
    val letters: List<Char> get() = text.toList()
}
