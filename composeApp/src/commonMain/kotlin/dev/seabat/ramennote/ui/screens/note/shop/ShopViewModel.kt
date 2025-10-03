package dev.seabat.ramennote.ui.screens.note.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopViewModel(
    private val loadImageUseCase: LoadImageUseCaseContract
) : ViewModel(), ShopViewModelContract {
    
    private val _shopImage = MutableStateFlow<ByteArray?>(null)
    override val shopImage: StateFlow<ByteArray?> = _shopImage.asStateFlow()
    
    override fun loadImage(shop: Shop) {
        viewModelScope.launch {
            val name = shop.photoName1

            if (name.isEmpty()) {
                _shopImage.value = null
                return@launch
            }

            when (val result = loadImageUseCase(name)) {
                is RunStatus.Success -> _shopImage.value = result.data
                is RunStatus.Error -> _shopImage.value = null
                is RunStatus.Loading -> { /* no-op */ }
                is RunStatus.Idle -> { /* no-op */ }
            }
        }
    }
}
