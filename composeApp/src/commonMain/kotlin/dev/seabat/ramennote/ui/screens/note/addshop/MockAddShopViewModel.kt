package dev.seabat.ramennote.ui.screens.note.addshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAddShopViewModel : AddShopViewModelContract {
    private val _saveState: MutableStateFlow<RunStatus<String>> = MutableStateFlow(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState.asStateFlow()

    override fun saveShop(shop: Shop) {
        // Preview用なので何もしない
    }
}