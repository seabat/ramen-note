package dev.seabat.ramennote.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.MonthlyReportCount
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.usecase.LoadFavoriteShopsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImagePathUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadImageUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadRecentFullReportsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadRecentScheduleUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadYearlyReportStatsUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateScheduleInShopUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.share.XShareLauncher
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
    private val loadImagePathUseCase: LoadImagePathUseCaseContract,
    private val loadRecentFullReportsUseCase: LoadRecentFullReportsUseCaseContract,
    private val loadYearlyReportStatsUseCase: LoadYearlyReportStatsUseCaseContract,
    private val updateScheduleInShopUseCase: UpdateScheduleInShopUseCaseContract
) : ViewModel(),
    HomeViewModelContract {
    private val _schedule = MutableStateFlow<Schedule?>(null)
    override val schedule: StateFlow<Schedule?> = _schedule.asStateFlow()

    private val _favoriteShops = MutableStateFlow<List<ShopWithImage>>(emptyList())
    override val favoriteShops: StateFlow<List<ShopWithImage>> = _favoriteShops.asStateFlow()

    private val _recentReports = MutableStateFlow<List<FullReport>>(emptyList())
    override val recentReports: StateFlow<List<FullReport>> = _recentReports.asStateFlow()

    private val _yearlyReportStats = MutableStateFlow<List<MonthlyReportCount>>(emptyList())
    override val yearlyReportStats: StateFlow<List<MonthlyReportCount>> = _yearlyReportStats.asStateFlow()

    override fun shareToX(postText: String, photoName: String, xShareLauncher: XShareLauncher) {
        viewModelScope.launch {
            val imageBytes =
                when (val result = loadImageUseCase(photoName)) {
                    is RunStatus.Success -> result.data
                    else -> null
                }
            xShareLauncher.shareToX(postText, imageBytes?.let { SharedImage(it) })
        }
    }

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
                val imagePath =
                    if (shop.photoName1.isNotBlank()) {
                        loadImagePathUseCase(shop.photoName1)
                    } else {
                        null
                    }
                val shopWithImage = ShopWithImage(shop = shop, imagePath = imagePath)
                _favoriteShops.value = _favoriteShops.value + shopWithImage
                delay(30) // 30ms遅延
            }
        }
    }

    override fun loadRecentReports() {
        viewModelScope.launch {
            _recentReports.value = loadRecentFullReportsUseCase()
        }
    }

    override fun loadYearlyReportStats() {
        viewModelScope.launch {
            val reports = loadYearlyReportStatsUseCase()

            // まず、すべてのcountを0にして更新
            _yearlyReportStats.value = reports.map { it.copy(count = 0) }

            // reportsから1件ずつ取り出し、yearMonthが一致する要素のcountを更新
            reports.forEach { report ->
                _yearlyReportStats.value =
                    _yearlyReportStats.value.map { item ->
                        if (item.yearMonth == report.yearMonth) {
                            item.copy(count = report.count)
                        } else {
                            item
                        }
                    }
                delay(50) // 50ms遅延
            }
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
