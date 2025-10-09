package dev.seabat.ramennote.ui.screens.home

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockHomeViewModel : HomeViewModelContract {
    private val _scheduleShop = MutableStateFlow<Shop>(Shop())
    override val scheduledShop: StateFlow<Shop> = _scheduleShop.asStateFlow()

    private val _scheduleShopState = MutableStateFlow<RunStatus<Shop?>>(RunStatus.Idle())
    override val scheduledShopState: StateFlow<RunStatus<Shop?>> = _scheduleShopState.asStateFlow()

    private val _favoriteShops = MutableStateFlow<List<Shop>>(emptyList())
    override val favoriteShops: StateFlow<List<Shop>> = _favoriteShops.asStateFlow()

    override fun loadRecentSchedule() {
        // Preview用なので何もしない
    }

    override fun setScheduledShopStateToIdle() {
        // Preview用なので何もしない
    }

    override fun loadFavoriteShops() {
        // Preview用なので何もしない
    }
}