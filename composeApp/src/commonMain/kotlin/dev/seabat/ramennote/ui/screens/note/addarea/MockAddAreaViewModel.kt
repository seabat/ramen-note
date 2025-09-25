package dev.seabat.ramennote.ui.screens.note.addarea

import androidx.lifecycle.ViewModel

/**
 * Preview用のモックAddAreaViewModel
 * 実際のデータベースアクセスを行わず、何もしない
 */
class MockAddAreaViewModel : ViewModel(), AddAreaViewModelContract {
    override fun addArea(area: String) {
        // Preview用なので何もしない
    }
}