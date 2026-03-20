package dev.seabat.ramennote.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadAreasUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsByAreaUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsByShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadFullReportsUseCaseContract
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
    private val searchShopsByNameUseCase: SearchShopsByNameUseCaseContract
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

    override fun loadReports() {
        viewModelScope.launch {
            _shopName.value = ""
            _areaName.value = ""
            _reports.value = loadReportsUseCase.invoke()
        }
    }

    override fun loadReportsByShop(shopId: Int) {
        viewModelScope.launch {
            _areaName.value = ""
            _reports.value = loadReportsByShopUseCase.invoke(shopId)
            // 店舗名は FullReport.shopName から取得
            _shopName.value = _reports.value.firstOrNull()?.shopName ?: ""
        }
    }

    override fun loadReportsByArea(areaId: Int) {
        viewModelScope.launch {
            _shopName.value = ""
            _reports.value = loadReportsByAreaUseCase.invoke(areaId)
            // エリア名を areas テーブルから取得
            _areaName.value = loadAreasUseCase().find { it.areaId == areaId }?.name ?: ""
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
