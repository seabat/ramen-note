package dev.seabat.ramennote.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsByAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsByShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadReportsByYearUseCaseContract
import dev.seabat.ramennote.domain.usecase.SearchShopsByNameUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.share.XShareLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val loadReportsUseCase: LoadFullReportsUseCaseContract,
    private val loadReportsByShopUseCase: LoadFullReportsByShopUseCaseContract,
    private val loadReportsByAreaUseCase: LoadFullReportsByAreaUseCaseContract,
    private val loadAreasUseCase: LoadAreasUseCaseContract,
    private val searchShopsByNameUseCase: SearchShopsByNameUseCaseContract,
    private val loadReportsByYearUseCase: LoadReportsByYearUseCaseContract
) : ViewModel(),
    HistoryViewModelContract {
    private val _reports = MutableStateFlow<List<FullReport>>(emptyList())
    override val reports: StateFlow<List<FullReport>> = _reports.asStateFlow()

    private val _shopName = MutableStateFlow<String>("")
    override val shopName: StateFlow<String> = _shopName.asStateFlow()

    private val _shops = MutableStateFlow<List<Shop>>(emptyList())
    override val shops: StateFlow<List<Shop>> = _shops.asStateFlow()

    private val _areaName = MutableStateFlow<String>("")
    override val areaName: StateFlow<String> = _areaName.asStateFlow()

    private val _year = MutableStateFlow<String>("")
    override val year: StateFlow<String> = _year.asStateFlow()

    private val _selectableYears = MutableStateFlow<List<Int>>(emptyList())
    override val selectableYears: StateFlow<List<Int>> = _selectableYears.asStateFlow()

    override fun loadReports() {
        viewModelScope.launch {
            _shopName.value = ""
            _areaName.value = ""
            _year.value = ""
            _reports.value = loadReportsUseCase.invoke()
            _selectableYears.value =
                _reports.value
                    .map { it.date.year }
                    .distinct()
                    .sortedDescending()
        }
    }

    override fun loadReportsByShop(shopId: Int) {
        viewModelScope.launch {
            // 店舗でフィルタリングする場合は他のフィルタリングを解除
            _areaName.value = ""
            _year.value = ""

            _reports.value = loadReportsByShopUseCase.invoke(shopId)
            // 店舗名は FullReport.shopName から取得
            _shopName.value = _reports.value.firstOrNull()?.shopName ?: ""
        }
    }

    override fun loadReportsByArea(areaId: Int) {
        viewModelScope.launch {
            // エリアでフィルタリングする場合は他のフィルタリングを解除
            _shopName.value = ""
            _year.value = ""

            _reports.value = loadReportsByAreaUseCase.invoke(areaId)
            // エリア名を areas テーブルから取得
            _areaName.value = loadAreasUseCase().find { it.areaId == areaId }?.name ?: ""
        }
    }

    override fun loadReportsByYear(year: Int) {
        viewModelScope.launch {
            // 年でフィルタリングする場合は他のフィルタリングを解除
            _shopName.value = ""
            _areaName.value = ""

            _reports.value = loadReportsByYearUseCase.invoke(year)
            _year.value = year.toString()
        }
    }

    override fun shareToX(
        postText: String,
        image: SharedImage?,
        xShareLauncher: XShareLauncher
    ) {
        viewModelScope.launch {
            xShareLauncher.shareToX(postText, image)
        }
    }

    override fun searchShops(query: String) {
        viewModelScope.launch {
            _shops.value = searchShopsByNameUseCase(query)
        }
    }
}
