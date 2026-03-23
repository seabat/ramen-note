package dev.seabat.ramennote.ui.screens.note.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.SwitchFavoriteUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateScheduleInShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateStarUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShopViewModel(
    private val loadShopUseCase: LoadShopUseCaseContract,
    private val loadImageUseCase: LoadImageUseCaseContract,
    private val addScheduleUseCase: UpdateScheduleInShopUseCaseContract,
    private val switchFavoriteUseCase: SwitchFavoriteUseCaseContract,
    private val updateStarUseCase: UpdateStarUseCaseContract,
    private val loadAreasUseCase: LoadAreasUseCaseContract
) : ViewModel(),
    ShopViewModelContract {
    private val _shop = MutableStateFlow<Shop?>(null)
    override val shop: StateFlow<Shop?> = _shop.asStateFlow()

    private val _shopImage = MutableStateFlow<ByteArray?>(null)
    override val shopImage: StateFlow<ByteArray?> = _shopImage.asStateFlow()

    private val _areaName = MutableStateFlow<String>("")
    override val areaName: StateFlow<String> = _areaName.asStateFlow()

    override fun loadShopAndImage(id: Int) {
        viewModelScope.launch {
            val shop = loadShopUseCase.invoke(id)
            _shop.value = shop
            _shopImage.value = shop?.let { loadShopImage(it.photoName1) }
            shop?.let {
                val areas = loadAreasUseCase()
                _areaName.value = areas.find { area -> area.areaId == it.areaId }?.name ?: ""
            }
        }
    }

    private suspend fun loadShopImage(photoName: String): ByteArray? {
        if (photoName.isEmpty()) return null
        val result = loadImageUseCase(photoName)
        return (result as? RunStatus.Success)?.data
    }

    override fun addSchedule(shopId: Int, date: kotlinx.datetime.LocalDate) {
        viewModelScope.launch {
            addScheduleUseCase(shopId, date)
            // スケジュール変更後に Shop データを再読み込み
            loadShopAndImage(shopId)
        }
    }

    override fun switchFavorite(onOff: Boolean, shopId: Int) {
        viewModelScope.launch {
            switchFavoriteUseCase(onOff, shopId)
            // お気に入り状態を更新した後、Shopデータを再読み込み
            loadShopAndImage(shopId)
        }
    }

    override fun updateStar(star: Int, shopId: Int) {
        viewModelScope.launch {
            updateStarUseCase(shopId, star)
            // 星評価を更新した後、Shopデータを再読み込み
            loadShopAndImage(shopId)
        }
    }
}
