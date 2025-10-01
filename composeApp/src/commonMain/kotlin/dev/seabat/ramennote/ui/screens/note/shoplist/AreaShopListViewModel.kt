package dev.seabat.ramennote.ui.screens.note.shoplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AreaShopListViewModel(
    private val shopsRepository: ShopsRepositoryContract
) : ViewModel(), AreaShopListViewModelContract {
    
    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    override val shops: StateFlow<List<Shop>> = _shops.asStateFlow()
    
    // init での自動読み込みを削除
    // 画面表示時に手動で呼び出す
    
    override fun loadShops(area: String) {
        viewModelScope.launch {
            try {
                val shopsList = shopsRepository.getShopsByArea(area)
                _shops.value = shopsList
            } catch (e: Exception) {
                // エラーハンドリング
                _shops.value = emptyList()
            }
        }
    }
}