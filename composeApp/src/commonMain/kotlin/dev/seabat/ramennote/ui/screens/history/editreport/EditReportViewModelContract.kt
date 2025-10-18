package dev.seabat.ramennote.ui.screens.history.editreport

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface EditReportViewModelContract {
    val fullReport: StateFlow<FullReport>
    val editedStatus: StateFlow<RunStatus<Int>>
    val deletedStatus: StateFlow<RunStatus<String>>

    fun loadReport(reportId: Int)
    fun editReport(menuName: String, reportedDate: LocalDate, impression: String, shopId: Int, image: SharedImage?)
    fun deleteReport(reportId: Int)
    fun setReportedStatusToIdle()
    fun setDeletedStatusToIdle()
}