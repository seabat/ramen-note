package dev.seabat.ramennote.ui.screens.settings

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface SettingsViewModelContract {
    val versionName: StateFlow<String>
    val versionNameState: StateFlow<RunStatus<String>>

    fun inquiryAppVersion()
}
