package dev.seabat.ramennote.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadRecentScheduleUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadRecentScheduleUseCase: LoadRecentScheduleUseCaseContract
) : ViewModel(), HomeViewModelContract {

    private val _scheduledShop = MutableStateFlow<Shop?>(null)
    override val scheduledShop: StateFlow<Shop?> = _scheduledShop.asStateFlow()
    
    private val _scheduledShopState = MutableStateFlow<RunStatus<Shop?>>(RunStatus.Idle())
    override val scheduledShopState: StateFlow<RunStatus<Shop?>> = _scheduledShopState.asStateFlow()

    override fun loadRecentSchedule() {
        viewModelScope.launch {
            _scheduledShopState.value = RunStatus.Loading()
            val result = loadRecentScheduleUseCase()
            _scheduledShopState.value = result
            
            when (result) {
                is RunStatus.Success -> {
                    _scheduledShop.value = result.data
                }
                is RunStatus.Error -> {
                    _scheduledShop.value = null
                }
                is RunStatus.Loading -> {
                    // Loading状態は既に設定済み
                }
                is RunStatus.Idle -> {}
            }
        }
    }

    override fun setScheduledShopStateToIdle() {
        _scheduledShopState.value = RunStatus.Idle()
    }
}