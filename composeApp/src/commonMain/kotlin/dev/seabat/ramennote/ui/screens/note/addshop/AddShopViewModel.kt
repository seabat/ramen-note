package dev.seabat.ramennote.ui.screens.note.addshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.usecase.AddShopUseCaseContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.SaveShopMenuImageUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddShopViewModel(
    private val addShopUseCase: AddShopUseCaseContract,
    private val saveShopMenuImageUseCase: SaveShopMenuImageUseCaseContract
) : ViewModel(), AddShopViewModelContract {

    private val _saveState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState

    override fun saveShop(shop: Shop, sharedImage: SharedImage?) {
        viewModelScope.launch {
            _saveState.value = RunStatus.Loading()
            try {
                // 画像を保存
                if (sharedImage != null && shop.photoName1.isNotEmpty()) {
                    val byteArray = sharedImage.toByteArray()
                    saveShopMenuImageUseCase(shop.photoName1, byteArray!!)
                }
                
                // 店舗情報を保存
                addShopUseCase.addShop(shop)
                _saveState.value = RunStatus.Success("")
            } catch (e: Exception) {
                _saveState.value = RunStatus.Error("店舗の保存に失敗しました: ${e.message}")
            }
        }
    }
}
