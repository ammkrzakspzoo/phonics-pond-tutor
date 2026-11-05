package com.phonicspond.tutor.data.repository

import com.phonicspond.tutor.data.local.PondPreferences
import com.phonicspond.tutor.domain.model.PondSettings
import com.phonicspond.tutor.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(private val prefs: PondPreferences) : SettingsRepository {

    private val state = MutableStateFlow(read())

    override val settings: StateFlow<PondSettings> = state.asStateFlow()

    override fun setSoundCues(enabled: Boolean) {
        prefs.soundCues = enabled
        state.value = read()
    }

    override fun setAutoHints(enabled: Boolean) {
        prefs.autoHints = enabled
        state.value = read()
    }

    private fun read(): PondSettings = PondSettings(
        soundCues = prefs.soundCues,
        autoHints = prefs.autoHints
    )
}
