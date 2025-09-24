package dev.seabat.ramennote.ui.viewmodel.mock

import androidx.lifecycle.ViewModel
import dev.seabat.ramennote.ui.viewmodel.AddAreaViewModelContract

/**
 * Preview用のモックAddAreaViewModel
 * 実際のデータベースアクセスを行わず、何もしない
 */
class MockAddAreaViewModel : ViewModel(), AddAreaViewModelContract {
    override fun addArea(area: String) {
        // Preview用なので何もしない
    }
}
