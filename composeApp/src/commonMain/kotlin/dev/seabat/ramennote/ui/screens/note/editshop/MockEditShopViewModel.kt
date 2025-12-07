package dev.seabat.ramennote.ui.screens.note.editshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockEditShopViewModel : EditShopViewModelContract {
    private val _saveState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState

    private val _deleteState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val deleteState: StateFlow<RunStatus<String>> = _deleteState

    private val _shopImage = MutableStateFlow<SharedImage?>(null)
    override val shopImage: StateFlow<SharedImage?> = _shopImage

    private val _areasState = MutableStateFlow<List<String>>(listOf("東京", "大阪", "京都", "北海道"))
    override val areasState: StateFlow<List<String>> = _areasState

    override fun loadImage(shop: Shop) {
        _shopImage.value = null
    }

    override fun setImage(sharedImage: SharedImage?) {
        _shopImage.value = null
    }

    override fun updateShop(shop: Shop, sharedImage: SharedImage?, oldArea: String) {
        _saveState.value = RunStatus.Success("")
    }

    override fun deleteShop(shopId: Int) {
        _shopImage.value = null
    }

    override fun loadAreas() {
        // Mock implementation - do nothing
    }

    override fun setSaveStateToIdle() {
        _saveState.value = RunStatus.Idle()
    }

    override fun setDeleteStateToIdle() {
        _deleteState.value = RunStatus.Idle()
    }
}
