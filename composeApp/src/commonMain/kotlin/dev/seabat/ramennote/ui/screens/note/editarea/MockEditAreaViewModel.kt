package dev.seabat.ramennote.ui.screens.note.editarea

import androidx.lifecycle.ViewModel
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Preview用のモックEditAreaViewModel
 * 実際のデータベースアクセスを行わず、固定のデータを返す
 */
class MockEditAreaViewModel :
    ViewModel(),
    EditAreaViewModelContract {
    override val deleteState: StateFlow<RunStatus<String>> = MutableStateFlow<RunStatus<String>>(RunStatus.Idle()).asStateFlow()
    override val editState: StateFlow<RunStatus<String>> = MutableStateFlow<RunStatus<String>>(RunStatus.Idle()).asStateFlow()
    override val imageState: StateFlow<RunStatus<ByteArray>> = MutableStateFlow<RunStatus<ByteArray>>(RunStatus.Idle()).asStateFlow()
    override val areaName: StateFlow<String> = MutableStateFlow("エリア名").asStateFlow()

    override fun editArea(areaId: Int, newAreaName: String) {
        // Preview用なので何もしない
    }

    override fun deleteArea(areaId: Int) {
        // Preview用なので何もしない
    }

    override fun fetchNewImage(areaName: String) {
        // Preview用なので何もしない
    }

    override fun loadAreaName(areaId: Int) {
        // Preview用なので何もしない
    }

    override fun loadImage(areaId: Int) {
        // Preview用なので何もしない
    }

    override fun resetImageState() {
        // Preview用なので何もしない
    }
}
