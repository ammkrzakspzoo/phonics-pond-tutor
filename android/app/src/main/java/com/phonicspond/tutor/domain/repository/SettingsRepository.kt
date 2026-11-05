package com.phonicspond.tutor.domain.repository

import com.phonicspond.tutor.domain.model.PondSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {

    val settings: StateFlow<PondSettings>

    fun setSoundCues(enabled: Boolean)

    fun setAutoHints(enabled: Boolean)
}
