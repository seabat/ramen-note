package dev.seabat.ramennote.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreasRepository
import dev.seabat.ramennote.domain.model.Area
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddAreaViewModel: ViewModel() {

    // TODO: DI 導入時にプライマリコンストラクタに移動
    private val areasRepository: AreasRepository = AreasRepository()

    fun addArea(name: String) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        viewModelScope.launch {
            areasRepository.add(
                Area(
                    name = name.trim(),
                    updatedDate = today,
                    count = 0
                )
            )
        }
    }
}


