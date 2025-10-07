package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.flow.StateFlow

interface HomeViewModelContract {
    val scheduledShop: StateFlow<Shop?>
    val scheduledShopState: StateFlow<RunStatus<Shop?>>
    fun loadRecentSchedule()
    fun setScheduledShopStateToIdle()
}