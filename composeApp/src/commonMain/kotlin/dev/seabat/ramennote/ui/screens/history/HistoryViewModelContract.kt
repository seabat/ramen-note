package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.Report
import kotlinx.coroutines.flow.StateFlow

interface HistoryViewModelContract {
    val reports: StateFlow<List<Report>>
    fun loadReports()
}