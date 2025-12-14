package dev.seabat.ramennote.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.InquiryAppVersionUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val inquiryAppVersionUseCase: InquiryAppVersionUseCaseContract
) : ViewModel(),
    SettingsViewModelContract {
    private val _versionNameState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val versionNameState: StateFlow<RunStatus<String>> = _versionNameState.asStateFlow()

    override val versionName: StateFlow<String> =
        _versionNameState.map { status ->
            when (status) {
                is RunStatus.Success -> status.data ?: ""
                else -> ""
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    override fun inquiryAppVersion() {
        viewModelScope.launch {
            _versionNameState.value = RunStatus.Loading()
            val result = inquiryAppVersionUseCase()
            _versionNameState.value = result
        }
    }
}

