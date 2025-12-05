package dev.seabat.ramennote.ui.screens.note.addshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAddShopViewModel : AddShopViewModelContract {
    private val _saveState: MutableStateFlow<RunStatus<String>> = MutableStateFlow(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState.asStateFlow()

    override fun saveShop(shop: Shop, sharedImage: SharedImage?) {
        // Preview用なので何もしない
    }

    override fun setSaveStateToIdle() {
        // Preview用なので何もしない
    }

    override fun createNoImage(): ByteArray = ByteArray(0)
}
