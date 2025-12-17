package dev.seabat.ramennote.ui.screens.history.addreport

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.share.XShareLauncher
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

interface AddReportViewModelContract {
    val reportedStatus: StateFlow<RunStatus<Int>>

    fun report(menuName: String, reportedDate: LocalDate, impression: String, shopId: Int, image: SharedImage?)

    fun shareToX(
        shopName: String,
        menuName: String,
        impression: String,
        image: SharedImage,
        xShareLauncher: XShareLauncher
    )

    fun setReportedStatusToIdle()
}
