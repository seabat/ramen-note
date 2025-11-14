package dev.seabat.ramennote.ui.screens.history

import dev.seabat.ramennote.domain.model.FullReport
import kotlinx.coroutines.flow.StateFlow

interface HistoryViewModelContract {
    val reports: StateFlow<List<FullReport>>

    fun loadReports()
}
