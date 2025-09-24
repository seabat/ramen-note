package dev.seabat.ramennote.ui.screens.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.Area
import kotlinx.coroutines.launch

class NoteViewModel(
    private val areasRepository: AreasRepositoryContract
): ViewModel(), NoteViewModelContract {

    private val _areas: MutableStateFlow<List<Area>> = MutableStateFlow(emptyList())
    override val areas: StateFlow<List<Area>> = _areas.asStateFlow()

    override fun fetchAreas() {
        viewModelScope.launch{
            _areas.value = areasRepository.fetch()
        }
    }
}


