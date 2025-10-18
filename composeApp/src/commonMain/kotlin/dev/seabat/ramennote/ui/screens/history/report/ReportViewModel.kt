package dev.seabat.ramennote.ui.screens.history.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.AddReportUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ReportViewModel(
    private val addReportUseCase: AddReportUseCaseContract
) : ViewModel(), ReportViewModelContract {
    private val _image = MutableStateFlow<SharedImage?>(null)
    override val image: StateFlow<SharedImage?> = _image.asStateFlow()
    
    private val _reportedStatus = MutableStateFlow<RunStatus<Int>>(RunStatus.Idle())
    override val reportedStatus: StateFlow<RunStatus<Int>> = _reportedStatus.asStateFlow()

    override fun report(
        menuName: String,
        reportedDate: LocalDate,
        impression: String,
        shopId: Int,
        image: SharedImage?
    ) {
        viewModelScope.launch {
            try {
                _reportedStatus.value = RunStatus.Loading()
                
                // AddReportUseCaseで画像保存とSQLite保存
                val result = addReportUseCase.invoke(
                    report = Report(
                        id = 0, // 後で更新される
                        shopId = shopId,
                        menuName = menuName,
                        photoName = createPhotoName(),
                        impression = impression,
                        date = reportedDate
                    ),
                    byteArray = image?.toByteArray()
                )
                _reportedStatus.value = result
                
            } catch (e: Exception) {
                _reportedStatus.value = RunStatus.Error(e.message ?: "レポートの保存に失敗しました")
            }
        }
    }

    override fun setReportedStatusToIdle() {
        _reportedStatus.value = RunStatus.Idle()
    }
    
    private fun createPhotoName(): String {
        val now = Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()
        )
        val currentTime = "${now.year}${now.monthNumber.toString()
            .padStart(2, '0')}${now.dayOfMonth.toString()
            .padStart(2, '0')}T${now.hour.toString()
            .padStart(2, '0')}${now.minute.toString()
            .padStart(2, '0')}${now.second.toString()
            .padStart(2, '0')}"
        return "R_$currentTime"
    }
}
