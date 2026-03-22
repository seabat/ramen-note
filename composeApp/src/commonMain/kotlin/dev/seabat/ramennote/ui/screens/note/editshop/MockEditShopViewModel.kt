package dev.seabat.ramennote.ui.screens.note.editshop

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDate

class MockEditShopViewModel : EditShopViewModelContract {
    private val _saveState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState

    private val _deleteState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val deleteState: StateFlow<RunStatus<String>> = _deleteState

    private val _shopImage = MutableStateFlow<SharedImage?>(null)
    override val shopImage: StateFlow<SharedImage?> = _shopImage

    private val _areasState = MutableStateFlow<List<Area>>(emptyList())
    override val areasState: StateFlow<List<Area>> = _areasState

    init {
        _areasState.value =
            listOf(
                Area(areaId = 1, name = "東京", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 1),
                Area(areaId = 2, name = "大阪", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 2),
                Area(areaId = 3, name = "京都", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 3),
                Area(areaId = 4, name = "北海道", count = 0, updatedDate = LocalDate(2024, 1, 1), sort = 4)
            )
    }

    override fun loadImage(shop: Shop) {
        _shopImage.value = null
    }

    override fun setImage(sharedImage: SharedImage?) {
        _shopImage.value = null
    }

    override fun updateShop(shop: Shop, sharedImage: SharedImage?, oldAreaId: Int) {
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
