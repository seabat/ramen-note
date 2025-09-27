package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class DeleteAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : DeleteAreaUseCaseContract {
    override suspend fun invoke(name: String): RunStatus<String> {
        val result = areasRepository.delete( name)
        if (result is RunStatus.Success) {
            localAreaImageRepository.delete(name)
        }
        return result
    }
}