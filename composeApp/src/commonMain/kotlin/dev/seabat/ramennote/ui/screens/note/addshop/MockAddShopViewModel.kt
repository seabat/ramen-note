package dev.seabat.ramennote.ui.screens.note.addshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopAiInfo
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAddShopViewModel : AddShopViewModelContract {
    private val _saveState: MutableStateFlow<RunStatus<String>> = MutableStateFlow(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState.asStateFlow()

    private val _shopAiInfoState = MutableStateFlow<RunStatus<ShopAiInfo>>(RunStatus.Idle())
    override val shopAiInfoState: StateFlow<RunStatus<ShopAiInfo>> = _shopAiInfoState.asStateFlow()

    override fun saveShop(shop: Shop, sharedImage: SharedImage?) {
        // Preview用なので何もしない
    }

    override fun setSaveStateToIdle() {
        // Preview用なので何もしない
    }

    override fun createNoImage(): ByteArray = ByteArray(0)

    override fun fetchShopAiInfo(areaName: String, shopName: String) {
        // Preview用なので何もしない
    }

    override fun setShopAiInfoStateToIdle() {
        // Preview用なので何もしない
    }
}
