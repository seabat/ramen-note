package dev.seabat.ramennote.ui.screens.history.editreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.Report
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.CreateReportPhotoNameUseCaseContract
import dev.seabat.ramennote.domain.usecase.DeleteReportUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateReportUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class EditReportViewModel(
    private val createReportPhotoNameUseCase: CreateReportPhotoNameUseCaseContract,
    private val loadFullReportUseCase: LoadFullReportUseCaseContract,
    private val deleteReportUseCase: DeleteReportUseCaseContract,
    private val updateReportUseCase: UpdateReportUseCaseContract
) : ViewModel(),
    EditReportViewModelContract {
    private val _fullReport = MutableStateFlow<FullReport>(FullReport())
    override val fullReport: StateFlow<FullReport> = _fullReport.asStateFlow()

    private val _editedStatus = MutableStateFlow<RunStatus<Int>>(RunStatus.Idle())
    override val editedStatus: StateFlow<RunStatus<Int>> = _editedStatus.asStateFlow()

    private val _deletedStatus = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val deletedStatus: StateFlow<RunStatus<String>> = _deletedStatus.asStateFlow()

    override fun loadReport(reportId: Int) {
        viewModelScope.launch {
            _fullReport.value = loadFullReportUseCase(reportId) ?: FullReport()
        }
    }

    override fun editReport(
        menuName: String,
        reportedDate: LocalDate,
        impression: String,
        shopId: Int,
        image: SharedImage?
    ) {
        viewModelScope.launch {
            try {
                _editedStatus.value = RunStatus.Loading()

                // UpdateReportUseCaseでSQLiteのReportを更新
                val currentReport = _fullReport.value
                updateReportUseCase.invoke(
                    report =
                        Report(
                            id = currentReport.id,
                            shopId = shopId,
                            menuName = menuName,
                            photoName =
                                if (image == null) {
                                    currentReport.photoName
                                } else {
                                    createReportPhotoNameUseCase()
                                },
                            impression = impression,
                            date = reportedDate
                        ),
                    imageBytes = image?.toByteArray()
                )
                _editedStatus.value = RunStatus.Success(currentReport.id)
            } catch (e: Exception) {
                _editedStatus.value = RunStatus.Error(e.message ?: "レポートの更新に失敗しました")
            }
        }
    }

    override fun deleteReport(reportId: Int) {
        viewModelScope.launch {
            _deletedStatus.value = RunStatus.Loading()
            try {
                deleteReportUseCase(reportId)
                _deletedStatus.value = RunStatus.Success("")
            } catch (e: Exception) {
                _deletedStatus.value = RunStatus.Error(e.message ?: "レポートの削除に失敗しました")
            }
        }
    }

    override fun setReportedStatusToIdle() {
        _editedStatus.value = RunStatus.Idle()
    }

    override fun setDeletedStatusToIdle() {
        _deletedStatus.value = RunStatus.Idle()
    }
}
