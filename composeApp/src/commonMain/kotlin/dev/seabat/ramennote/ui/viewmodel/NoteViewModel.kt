package dev.seabat.ramennote.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dev.seabat.ramennote.data.repository.AreasRepository
import dev.seabat.ramennote.domain.model.Area
import kotlinx.coroutines.launch

class NoteViewModel(): ViewModel() {

    // TODO: DI 導入時にプライマリコンストラクタに移動
    private val areasRepository: AreasRepository = AreasRepository()

    private val _areas: MutableStateFlow<List<Area>> = MutableStateFlow(emptyList())
    val areas: StateFlow<List<Area>> = _areas.asStateFlow()

    fun fetchAreas() {
        viewModelScope.launch{
            _areas.value = areasRepository.fetch()
        }
    }
}


