package dev.seabat.ramennote.ui.screens.note.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopViewModel(
    private val areaImageRepository: AreaImageRepositoryContract
) : ViewModel() {
    
    private val _shopImage = MutableStateFlow<ByteArray?>(null)
    val shopImage: StateFlow<ByteArray?> = _shopImage.asStateFlow()
    
    fun loadShopImage(shop: Shop) {
        viewModelScope.launch {
            try {
                val imageBytes = areaImageRepository.fetch()
                _shopImage.value = imageBytes
            } catch (e: Exception) {
                // エラーハンドリング
                _shopImage.value = null
            }
        }
    }
}
