package dev.seabat.ramennote.ui.screens.note.addshop

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.StateFlow

interface AddShopViewModelContract {
    val saveState: StateFlow<RunStatus<String>>

    fun saveShop(shop: Shop, sharedImage: SharedImage?)

    fun setSaveStateToIdle()

    fun createNoImage(): ByteArray
}
