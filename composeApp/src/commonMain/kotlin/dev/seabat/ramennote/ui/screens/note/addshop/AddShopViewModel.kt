package dev.seabat.ramennote.ui.screens.note.addshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddShopViewModel(
    private val shopsRepository: ShopsRepositoryContract
) : ViewModel(), AddShopViewModelContract {

    private val _saveState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState

    override fun saveShop(shop: Shop) {
        viewModelScope.launch {
            _saveState.value = RunStatus.Loading()
            try {
                shopsRepository.insertShop(shop)
                _saveState.value = RunStatus.Success("")
            } catch (e: Exception) {
                _saveState.value = RunStatus.Error("店舗の保存に失敗しました: ${e.message}")
            }
        }
    }
}
