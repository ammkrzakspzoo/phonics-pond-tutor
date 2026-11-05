package com.phonicspond.tutor.domain.model

object PhonicsPlan {

    val groups: List<List<Char>> = listOf(
        listOf('s', 'a', 't', 'p', 'i', 'n'),
        listOf('m', 'd', 'g', 'o', 'c', 'k'),
        listOf('e', 'u', 'r', 'h', 'b', 'f', 'l'),
        listOf('j', 'v', 'w', 'x', 'y', 'z')
    )

    val order: List<Char> = groups.flatten()

    fun activeGroup(masteredLetters: Set<Char>): List<Char> {
        val open = groups.firstOrNull { group -> group.any { it !in masteredLetters } }
        return open ?: groups.last()
    }

    fun focusLetter(masteredLetters: Set<Char>): Char {
        val group = activeGroup(masteredLetters)
        return group.firstOrNull { it !in masteredLetters } ?: group.first()
    }
}
