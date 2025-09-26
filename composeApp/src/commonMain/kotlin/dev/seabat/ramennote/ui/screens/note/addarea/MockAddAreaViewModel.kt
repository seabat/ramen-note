package dev.seabat.ramennote.ui.screens.note.addarea

import androidx.lifecycle.ViewModel
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Preview用のモックAddAreaViewModel
 * 実際のデータベースアクセスを行わず、何もしない
 */
class MockAddAreaViewModel : ViewModel(), AddAreaViewModelContract {
    private val _imageState: MutableStateFlow<RunStatus<ByteArray?>> =
        MutableStateFlow(RunStatus.Idle())
    override val imageState: StateFlow<RunStatus<ByteArray?>> = _imageState.asStateFlow()

    override fun addArea(area: String) {
        // Preview用なので何もしない
    }

    override fun fetchImage() {
        // Preview用なので何もしない
    }
}