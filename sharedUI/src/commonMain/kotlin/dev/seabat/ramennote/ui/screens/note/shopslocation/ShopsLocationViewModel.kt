package dev.seabat.ramennote.ui.screens.note.shopslocation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.ShopLocation
import dev.seabat.ramennote.domain.usecase.LoadShopListByAreaIdUseCaseContract
import dev.seabat.ramennote.domain.usecase.ResolveShopLocationUseCaseContract
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopsLocationViewModel(
    private val loadShopListByAreaIdUseCase: LoadShopListByAreaIdUseCaseContract,
    private val resolveShopLocationUseCase: ResolveShopLocationUseCaseContract
) : ViewModel(),
    ShopsLocationViewModelContract {
    private val _locations = MutableStateFlow<List<ShopLocation>>(emptyList())
    override val locations: StateFlow<List<ShopLocation>> = _locations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    override fun loadShopsAndResolveLocations(areaId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _locations.value = emptyList()

            val shops = loadShopListByAreaIdUseCase(areaId)

            // 全ショップの Geocoding を並列実行し、完了後にまとめてリストに反映
            val resolved =
                shops
                    .map { shop ->
                        async { resolveShopLocationUseCase(shop) }
                    }.awaitAll()
                    .filterNotNull()

            _locations.value = resolved
            _isLoading.value = false
        }
    }
}
