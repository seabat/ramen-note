package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus

interface UpdateAllAreasUseCaseContract {
    suspend operator fun invoke(areas: List<Area>): RunStatus<String>
}
