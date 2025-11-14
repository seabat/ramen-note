package dev.seabat.ramennote.ui.screens.history.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.AddReportUseCaseContract
import dev.seabat.ramennote.domain.usecase.CreateReportPhotoNameUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class ReportViewModel(
    private val createReportPhotoNameUseCase: CreateReportPhotoNameUseCaseContract,
    private val addReportUseCase: AddReportUseCaseContract
) : ViewModel(),
    ReportViewModelContract {
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
                val result =
                    addReportUseCase.invoke(
                        report =
                            Report(
                                id = 0, // 後で更新される
                                shopId = shopId,
                                menuName = menuName,
                                photoName = createReportPhotoNameUseCase(),
                                impression = impression,
                                date = reportedDate
                            ),
                        imageBytes = image?.toByteArray()
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
}
