package com.phonicspond.tutor.presentation.gameover

import androidx.lifecycle.ViewModel
import com.phonicspond.tutor.domain.model.ActivityKind
import com.phonicspond.tutor.domain.repository.ContentRepository
import com.phonicspond.tutor.domain.repository.ProgressRepository
import com.phonicspond.tutor.domain.usecase.NextActivityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

class GameOverViewModel(
    private val progressRepository: ProgressRepository,
    private val contentRepository: ContentRepository,
    private val nextActivityUseCase: NextActivityUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    fun show(wordId: String, activity: ActivityKind, stickerEarned: Boolean) {
        val word = contentRepository.wordById(wordId)
        val progress = progressRepository.progress.value
        _state.update {
            it.copy(
                word = word?.text.orEmpty(),
                phonemes = word?.phonemes.orEmpty(),
                stickerEarned = stickerEarned,
                wordsRead = progress.wordsRead.size,
                stickers = progress.stickers,
                masteredLetters = progress.masteredLetters.size,
                spriteIndex = abs(wordId.hashCode()) % 3,
                nextActivity = nextActivityUseCase(activity)
            )
        }
    }
}
