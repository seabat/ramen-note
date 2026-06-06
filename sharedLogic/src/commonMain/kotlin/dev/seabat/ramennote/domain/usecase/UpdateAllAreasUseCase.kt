package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus

class UpdateAllAreasUseCase(
    private val areasRepository: AreasRepositoryContract
) : UpdateAllAreasUseCaseContract {
    override suspend fun invoke(areas: List<Area>): RunStatus<String> = areasRepository.editAll(areas)
}
