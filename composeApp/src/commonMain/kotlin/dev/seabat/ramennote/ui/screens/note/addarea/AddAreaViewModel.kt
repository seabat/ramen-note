package dev.seabat.ramennote.ui.screens.note.addarea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.Area
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AddAreaViewModel(private val areasRepository: AreasRepositoryContract): ViewModel(), AddAreaViewModelContract {

    override fun addArea(area: String) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        viewModelScope.launch {
            areasRepository.add(
                Area(
                    name = area.trim(),
                    updatedDate = today,
                    count = 0
                )
            )
        }
    }
}


