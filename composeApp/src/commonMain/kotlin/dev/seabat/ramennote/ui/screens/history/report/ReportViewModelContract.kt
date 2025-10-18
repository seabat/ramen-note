package dev.seabat.ramennote.ui.screens.history.report

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface ReportViewModelContract {
    val image: StateFlow<SharedImage?>
    val reportedStatus: StateFlow<RunStatus<Int>>

    fun report(menuName: String, reportedDate: LocalDate, impression: String, shopId: Int, image: SharedImage?)
    fun setReportedStatusToIdle()
}