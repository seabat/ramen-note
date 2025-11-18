package dev.seabat.ramennote.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.usecase.LoadFavoriteShopsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadRecentScheduleUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadThreeMonthsFullReportsUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateScheduleInShopUseCaseContract
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class HomeViewModel(
    private val loadRecentScheduleUseCase: LoadRecentScheduleUseCaseContract,
    private val loadFavoriteShopsUseCase: LoadFavoriteShopsUseCaseContract,
    private val loadImageUseCase: LoadImageUseCaseContract,
    private val loadThreeMonthsFullReportsUseCase: LoadThreeMonthsFullReportsUseCaseContract,
    private val updateScheduleInShopUseCase: UpdateScheduleInShopUseCaseContract
) : ViewModel(),
    HomeViewModelContract {
    private val _schedule = MutableStateFlow<Schedule?>(null)
    override val schedule: StateFlow<Schedule?> = _schedule.asStateFlow()

    private val _favoriteShops = MutableStateFlow<List<ShopWithImage>>(emptyList())
    override val favoriteShops: StateFlow<List<ShopWithImage>> = _favoriteShops.asStateFlow()

    private val _threeMonthsReports = MutableStateFlow<List<FullReport>>(emptyList())
    override val threeMonthsReports: StateFlow<List<FullReport>> = _threeMonthsReports.asStateFlow()

    /** 最新の予定読み込み状態 */
    private val _loadedScheduleState = MutableStateFlow<RunStatus<Schedule?>>(RunStatus.Idle())
    override val loadedScheduleState: StateFlow<RunStatus<Schedule?>> = _loadedScheduleState.asStateFlow()

    /** 予定追加状態 */
    private val _addedScheduleState = MutableStateFlow<RunStatus<String>>(RunStatus.Idle())
    override val addedScheduleState: StateFlow<RunStatus<String>> = _addedScheduleState.asStateFlow()

    override fun loadRecentSchedule() {
        viewModelScope.launch {
            _loadedScheduleState.value = RunStatus.Loading()
            val result = loadRecentScheduleUseCase()
            _loadedScheduleState.value = result

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

    override fun setLoadedScheduleStateToIdle() {
        _loadedScheduleState.value = RunStatus.Idle()
    }

    override fun loadFavoriteShops() {
        viewModelScope.launch {
            val favoriteShops = loadFavoriteShopsUseCase()
            _favoriteShops.value = emptyList() // リストをクリア

            favoriteShops.forEach { shop ->
                val imageBytes =
                    if (shop.photoName1.isNotBlank()) {
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

    override fun loadThreeMonthsReports() {
        viewModelScope.launch {
            _threeMonthsReports.value = loadThreeMonthsFullReportsUseCase()
        }
    }

    override fun addSchedule(shopId: Int, date: LocalDate) {
        viewModelScope.launch {
            _addedScheduleState.value = RunStatus.Loading()
            updateScheduleInShopUseCase(shopId, date)
            _addedScheduleState.value = RunStatus.Success("")
        }
    }

    override fun setAddedScheduleStateToIdle() {
        _addedScheduleState.value = RunStatus.Idle()
    }
}
