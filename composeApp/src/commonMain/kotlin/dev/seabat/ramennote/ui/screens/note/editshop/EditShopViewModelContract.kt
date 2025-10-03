package dev.seabat.ramennote.ui.screens.note.editshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.StateFlow

interface EditShopViewModelContract {
    val saveState: StateFlow<RunStatus<String>>
    val shopImage: StateFlow<ByteArray?>
    
    fun loadImage(shop: Shop)
    fun updateShop(shop: Shop, sharedImage: SharedImage?)
}
