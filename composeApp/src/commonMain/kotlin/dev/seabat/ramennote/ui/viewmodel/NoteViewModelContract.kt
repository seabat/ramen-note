package dev.seabat.ramennote.ui.viewmodel

import dev.seabat.ramennote.domain.model.Area
import kotlinx.coroutines.flow.StateFlow

interface NoteViewModelContract {
    val areas: StateFlow<List<Area>>
    fun fetchAreas()
}
