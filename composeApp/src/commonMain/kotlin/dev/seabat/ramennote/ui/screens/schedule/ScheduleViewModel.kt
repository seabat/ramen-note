package dev.seabat.ramennote.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.domain.model.Schedule
import dev.seabat.ramennote.domain.usecase.DeleteScheduleInShopUseCaseContract
import dev.seabat.ramennote.domain.usecase.LoadScheduledShopsUseCaseContract
import dev.seabat.ramennote.domain.usecase.UpdateScheduleInShopUseCaseContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class ScheduleViewModel(
    private val deleteScheduleInShopUseCase: DeleteScheduleInShopUseCaseContract,
    private val loadScheduledShopsUseCase: LoadScheduledShopsUseCaseContract,
    private val updateScheduleInShopUseCaseContract: UpdateScheduleInShopUseCaseContract
) : ViewModel(),
    ScheduleViewModelContract {
    private val _schedules: MutableStateFlow<List<Schedule>> = MutableStateFlow(emptyList())
    override val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    override fun loadSchedule() {
        viewModelScope.launch {
            val schedules = loadScheduledShopsUseCase()
            _schedules.value = schedules
        }
    }

    override fun editSchedule(shopId: Int, date: LocalDate) {
        viewModelScope.launch {
            updateScheduleInShopUseCaseContract(shopId, date)

            val schedules = loadScheduledShopsUseCase()
            _schedules.value = schedules
        }
    }

    override fun deleteSchedule(shopId: Int) {
        viewModelScope.launch {
            deleteScheduleInShopUseCase(shopId)

            val schedules = loadScheduledShopsUseCase()
            _schedules.value = schedules
        }
    }
}
