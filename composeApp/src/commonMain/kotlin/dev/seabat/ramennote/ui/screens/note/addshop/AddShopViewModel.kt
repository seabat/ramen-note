package dev.seabat.ramennote.ui.screens.note.addshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopAiInfo
import dev.seabat.ramennote.domain.usecase.AddShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.CreateNoImageByteArrayUseCaseContract
import dev.seabat.ramennote.domain.usecase.FetchAiShopUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddShopViewModel(
    private val addShopUseCase: AddShopUseCaseContract,
    private val createNoImageUseCase: CreateNoImageByteArrayUseCaseContract,
    private val fetchAiShopInfoUseCase: FetchAiShopUseCaseContract
) : ViewModel(),
    AddShopViewModelContract {
    private val _saveState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val saveState: StateFlow<RunStatus<String>> = _saveState

    private val _shopAiInfoState = MutableStateFlow<RunStatus<ShopAiInfo>>(RunStatus.Idle())
    override val shopAiInfoState: StateFlow<RunStatus<ShopAiInfo>> = _shopAiInfoState.asStateFlow()

    override fun saveShop(shop: Shop, sharedImage: SharedImage?) {
        viewModelScope.launch {
            _saveState.value = RunStatus.Loading()
            try {
                // 店舗情報と画像を保存
                addShopUseCase.invoke(shop, sharedImage?.toByteArray())
                _saveState.value = RunStatus.Success("")
            } catch (e: Exception) {
                _saveState.value = RunStatus.Error("店舗の保存に失敗しました: ${e.message}")
            }
        }
    }

    override fun setSaveStateToIdle() {
        _saveState.value = RunStatus.Idle()
    }

    override fun createNoImage(): ByteArray = createNoImageUseCase.invoke()

    override fun fetchShopAiInfo(areaName: String, shopName: String) {
        viewModelScope.launch {
            _shopAiInfoState.value = RunStatus.Loading()
            try {
                val result = fetchAiShopInfoUseCase(areaName, shopName)
                _shopAiInfoState.value = result
            } catch (e: Exception) {
                _shopAiInfoState.value = RunStatus.Error("店舗情報の取得に失敗しました: ${e.message}")
            }
        }
    }

    override fun setShopAiInfoStateToIdle() {
        _shopAiInfoState.value = RunStatus.Idle()
    }
}
