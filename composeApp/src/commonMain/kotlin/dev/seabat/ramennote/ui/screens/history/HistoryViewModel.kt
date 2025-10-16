package dev.seabat.ramennote.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.usecase.LoadReportsUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val loadReportsUseCase: LoadReportsUseCaseContract
) : ViewModel(), HistoryViewModelContract {

    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    override val reports: StateFlow<List<Report>> = _reports.asStateFlow()

    override fun loadReports() {
        viewModelScope.launch {
            _reports.value = loadReportsUseCase.invoke()
        }
    }
}


