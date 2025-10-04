package dev.seabat.ramennote.ui.screens.note.editshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MockEditShopViewModel : EditShopViewModelContract {
    private val _saveState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState

    private val _shopImage = MutableStateFlow<SharedImage?>(null)
    override val shopImage: StateFlow<SharedImage?> = _shopImage

    override fun loadImage(shop: Shop) {
        _shopImage.value = null
    }

    override fun updateImage(sharedImage: SharedImage?) {
        _shopImage.value = null
    }

    override fun updateShop(shop: Shop, sharedImage: SharedImage?) {
        _saveState.value = RunStatus.Success("")
    }
}