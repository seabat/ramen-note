package dev.seabat.ramennote.ui.screens.history.editreport

import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

/**
 * Preview用のモックEditReportViewModel
 * 実際のデータベースアクセスを行わず、固定のデータを返す
 */
class MockEditReportViewModel : EditReportViewModelContract {
    private val _fullReport = MutableStateFlow<FullReport>(
        FullReport(
            id = 1,
            shopId = 1,
            shopName = "サンプルラーメン店",
            menuName = "醤油ラーメン",
            photoName = "sample_photo.jpg",
            impression = "とても美味しかったです。スープが濃厚で、麺もコシがありました。",
            date = createTodayLocalDate(),
            imageBytes = null,
            star = 4
        )
    )
    override val fullReport: StateFlow<FullReport> = _fullReport.asStateFlow()

    private val _editedStatus = MutableStateFlow<RunStatus<Int>>(RunStatus.Idle())
    override val editedStatus: StateFlow<RunStatus<Int>> = _editedStatus.asStateFlow()

    private val _deletedStatus = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val deletedStatus: StateFlow<RunStatus<String>> = _deletedStatus.asStateFlow()

    override fun loadReport(reportId: Int) {
        // Preview用なので何もしない（既に初期値が設定されている）
    }

    override fun editReport(
        menuName: String,
        reportedDate: LocalDate,
        impression: String,
        shopId: Int,
        image: SharedImage?,
        star: Int
    ) {
        // Preview用なので何もしない
    }

    override fun deleteReport(reportId: Int) {
        // Preview用なので何もしない
    }

    override fun setReportedStatusToIdle() {
        _editedStatus.value = RunStatus.Idle()
    }

    override fun setDeletedStatusToIdle() {
        _deletedStatus.value = RunStatus.Idle()
    }
}
