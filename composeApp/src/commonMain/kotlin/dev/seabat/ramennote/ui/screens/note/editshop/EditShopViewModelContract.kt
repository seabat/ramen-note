package dev.seabat.ramennote.ui.screens.note.editshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.StateFlow

interface EditShopViewModelContract {
    val saveState: StateFlow<RunStatus<String>>
    val deleteState: StateFlow<RunStatus<String>>
    val shopImage: StateFlow<SharedImage?>
    val areasState: StateFlow<List<String>>

    fun loadImage(shop: Shop)

    fun setImage(sharedImage: SharedImage?)

    fun updateShop(shop: Shop, sharedImage: SharedImage?, oldArea: String)

    fun deleteShop(shopId: Int)

    fun loadAreas()

    fun setSaveStateToIdle()

    fun setDeleteStateToIdle()
}
