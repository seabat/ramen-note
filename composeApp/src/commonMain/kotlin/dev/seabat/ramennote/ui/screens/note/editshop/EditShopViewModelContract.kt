package dev.seabat.ramennote.ui.screens.note.editshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.StateFlow

interface EditShopViewModelContract {
    val saveState: StateFlow<RunStatus<String>>
    val deleteState: StateFlow<RunStatus<String>>
    val shopImage: StateFlow<SharedImage?>
    
    fun loadImage(shop: Shop)
    fun updateImage(sharedImage: SharedImage?)
    fun updateShop(shop: Shop, sharedImage: SharedImage?)
    fun deleteShop(shopId: Int)
    fun setSaveStateToIdle()
    fun setDeleteStateToIdle()
}
