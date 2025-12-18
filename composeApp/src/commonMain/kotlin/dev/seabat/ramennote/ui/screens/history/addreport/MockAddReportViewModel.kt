package dev.seabat.ramennote.ui.screens.history.addreport

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.share.XShareLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

/**
 * Preview用のモックReportViewModel
 * 実際のデータベースアクセスを行わず、固定のデータを返す
 */
class MockAddReportViewModel : AddReportViewModelContract {
    private val _reportedStatus: MutableStateFlow<RunStatus<Int>> =
        MutableStateFlow(RunStatus.Idle())
    override val reportedStatus: StateFlow<RunStatus<Int>> = _reportedStatus.asStateFlow()

    override fun report(
        menuName: String,
        reportedDate: LocalDate,
        impression: String,
        shopId: Int,
        image: SharedImage?
    ) {
        // Preview用なので何もしない
    }

    override fun shareToX(
        postText: String,
        image: SharedImage?,
        xShareLauncher: XShareLauncher
    ) {
        // Preview用なので何もしない
    }

    override fun setReportedStatusToIdle() {
        _reportedStatus.value = RunStatus.Idle()
    }
}
