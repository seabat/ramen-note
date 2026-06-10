package dev.seabat.ramennote.ui.screens.note.editshop

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.StateFlow

interface EditShopViewModelContract {
    val saveState: StateFlow<RunStatus<String>>
    val deleteState: StateFlow<RunStatus<String>>
    val shopImage: StateFlow<SharedImage?>
    val areasState: StateFlow<List<Area>>

    fun loadImage(shop: Shop)

    fun setImage(sharedImage: SharedImage?)

    fun updateShop(shop: Shop, sharedImage: SharedImage?, oldAreaId: Int)

    fun deleteShop(shopId: Int)

    fun loadAreas()

    fun setSaveStateToIdle()

    fun setDeleteStateToIdle()
}
