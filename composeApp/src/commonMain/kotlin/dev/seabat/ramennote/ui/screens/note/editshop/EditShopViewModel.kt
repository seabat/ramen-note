package dev.seabat.ramennote.ui.screens.note.editshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.usecase.UpdateShopUseCaseContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.SaveShopMenuImageUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditShopViewModel(
    private val updateShopUseCase: UpdateShopUseCaseContract,
    private val loadImageUseCase: LoadImageUseCaseContract,
    private val saveShopMenuImageUseCase: SaveShopMenuImageUseCaseContract
) : ViewModel(), EditShopViewModelContract {

    private val _saveState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState

    private val _shopImage = MutableStateFlow<SharedImage?>(null)
    override val shopImage: StateFlow<SharedImage?> = _shopImage.asStateFlow()

    override fun loadImage(shop: Shop) {
        viewModelScope.launch {
            val name = shop.photoName1
            if (name.isEmpty()) {
                _shopImage.value = null
                return@launch
            }
            when (val result = loadImageUseCase(name)) {
                is RunStatus.Success -> _shopImage.value = result.data?.let {
                    SharedImage(result.data)
                }
                is RunStatus.Error -> _shopImage.value = null
                is RunStatus.Loading -> { }
                is RunStatus.Idle -> { }
            }
        }
    }

    override fun updateImage(sharedImage: SharedImage?) {
        _shopImage.value = sharedImage
    }

    override fun updateShop(shop: Shop, sharedImage: SharedImage?) {
        viewModelScope.launch {
            _saveState.value = RunStatus.Loading()
            try {
                // 画像を保存
                if (sharedImage != null && shop.photoName1.isNotEmpty()) {
                    val byteArray = sharedImage.toByteArray()
                    saveShopMenuImageUseCase(shop.photoName1, byteArray!!)
                }
                
                // 店舗情報を更新
                updateShopUseCase.updateShop(shop)
                _saveState.value = RunStatus.Success("")
            } catch (e: Exception) {
                _saveState.value = RunStatus.Error("店舗の更新に失敗しました: ${e.message}")
            }
        }
    }
}
