package dev.seabat.ramennote.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.usecase.LoadRecentScheduleUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFavoriteShopsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class HomeViewModel(
    private val loadRecentScheduleUseCase: LoadRecentScheduleUseCaseContract,
    private val loadFavoriteShopsUseCase: LoadFavoriteShopsUseCaseContract,
    private val loadImageUseCase: LoadImageUseCaseContract
) : ViewModel(), HomeViewModelContract {

    private val _schedule = MutableStateFlow<Schedule?>(null)
    override val schedule: StateFlow<Schedule?> = _schedule.asStateFlow()
    
    private val _scheduleState = MutableStateFlow<RunStatus<Schedule?>>(RunStatus.Idle())
    override val scheduleState: StateFlow<RunStatus<Schedule?>> = _scheduleState.asStateFlow()
    
    private val _favoriteShops = MutableStateFlow<List<ShopWithImage>>(emptyList())
    override val favoriteShops: StateFlow<List<ShopWithImage>> = _favoriteShops.asStateFlow()

    override fun loadRecentSchedule() {
        viewModelScope.launch {
            _scheduleState.value = RunStatus.Loading()
            val result = loadRecentScheduleUseCase()
            _scheduleState.value = result
            
            when (result) {
                is RunStatus.Success -> {
                    _schedule.value = result.data
                }
                is RunStatus.Error -> {
                    _schedule.value = null
                }
                is RunStatus.Loading -> {
                    // Loading状態は既に設定済み
                }
                is RunStatus.Idle -> {}
            }
        }
    }

    override fun setScheduleStateToIdle() {
        _scheduleState.value = RunStatus.Idle()
    }
    
    override fun loadFavoriteShops() {
        viewModelScope.launch {
            val favoriteShops = loadFavoriteShopsUseCase()
            _favoriteShops.value = emptyList() // リストをクリア
            
            favoriteShops.forEach { shop ->
                val imageBytes = if (shop.photoName1.isNotBlank()) {
                    when (val status = loadImageUseCase(shop.photoName1)) {
                        is RunStatus.Success -> status.data
                        else -> null
                    }
                } else {
                    null
                }
                val shopWithImage = ShopWithImage(shop = shop, imageBytes = imageBytes)
                _favoriteShops.value = _favoriteShops.value + shopWithImage
                delay(30) // 30ms遅延
            }
        }
    }
}