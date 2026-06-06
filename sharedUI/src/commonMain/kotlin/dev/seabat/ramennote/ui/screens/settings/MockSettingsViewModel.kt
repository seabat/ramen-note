package dev.seabat.ramennote.ui.screens.settings

import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MockSettingsViewModel : SettingsViewModelContract {
    private val _versionNameState = MutableStateFlow<RunStatus<String>>(RunStatus.Success("1.0.0"))
    override val versionNameState: StateFlow<RunStatus<String>> = _versionNameState.asStateFlow()

    override val versionName: StateFlow<String> =
        _versionNameState
            .map { status ->
                when (status) {
                    is RunStatus.Success -> status.data ?: ""
                    else -> ""
                }
            }.stateIn(
                scope = CoroutineScope(Dispatchers.Default),
                started =
                    kotlinx.coroutines.flow.SharingStarted
                        .WhileSubscribed(5000),
                initialValue = "1.0.0"
            )

    override fun inquiryAppVersion() {
        // Preview用なので何もしない
    }
}
