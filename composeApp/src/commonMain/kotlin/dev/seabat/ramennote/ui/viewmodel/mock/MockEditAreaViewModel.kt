package dev.seabat.ramennote.ui.viewmodel.mock

import androidx.lifecycle.ViewModel
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.ui.viewmodel.EditAreaViewModelContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Preview用のモックEditAreaViewModel
 * 実際のデータベースアクセスを行わず、固定のデータを返す
 */
class MockEditAreaViewModel : ViewModel(), EditAreaViewModelContract {
    private val _deleteStatus: MutableStateFlow<RunStatus<String>> = MutableStateFlow(RunStatus.Idle())
    override val deleteStatus: StateFlow<RunStatus<String>> = _deleteStatus.asStateFlow()

    private val _editStatus: MutableStateFlow<RunStatus<String>> = MutableStateFlow(RunStatus.Idle())
    override val editStatus: StateFlow<RunStatus<String>> = _editStatus.asStateFlow()

    override var currentAreas: String = ""

    override fun editArea(newArea: String) {
        // Preview用なので何もしない
    }

    override fun deleteArea(area: String) {
        // Preview用なので何もしない
    }
}
