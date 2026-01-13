package dev.seabat.ramennote.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.FullReport
import dev.seabat.ramennote.domain.usecase.LoadFullReportsUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadShopUseCaseContract
import dev.seabat.ramennote.ui.gallery.SharedImage
import dev.seabat.ramennote.ui.share.XShareLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val loadReportsUseCase: LoadFullReportsUseCaseContract,
    private val loadShopUseCase: LoadShopUseCaseContract
) : ViewModel(),
    HistoryViewModelContract {
    private val _reports = MutableStateFlow<List<FullReport>>(emptyList())
    override val reports: StateFlow<List<FullReport>> = _reports.asStateFlow()

    private val _shopName = MutableStateFlow<String>("")
    override val shopName: StateFlow<String> = _shopName.asStateFlow()

    override fun loadReports(shopId: Int?) {
        viewModelScope.launch {
            _reports.value = loadReportsUseCase.invoke(shopId)
            if (shopId != null) {
                _shopName.value = loadShopUseCase.invoke(shopId)?.name ?: ""
            }
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
}
