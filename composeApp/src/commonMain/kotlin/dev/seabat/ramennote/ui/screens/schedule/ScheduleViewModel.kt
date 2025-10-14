package dev.seabat.ramennote.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.usecase.LoadScheduledShopsUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val loadScheduledShopsUseCase: LoadScheduledShopsUseCaseContract
) : ViewModel(), ScheduleViewModelContract {

    private val _scheduledShops: MutableStateFlow<List<Shop>> = MutableStateFlow(emptyList())
    override val scheduledShops: StateFlow<List<Shop>> = _scheduledShops.asStateFlow()

    override fun loadSchedule() {
        viewModelScope.launch {
            val shops = loadScheduledShopsUseCase()
            _scheduledShops.value = shops
        }
    }
}


