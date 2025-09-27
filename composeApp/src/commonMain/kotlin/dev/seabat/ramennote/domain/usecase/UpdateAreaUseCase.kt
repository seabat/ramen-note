package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class UpdateAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : UpdateAreaUseCaseContract {

    override suspend fun invoke(oldName: String, newName: String): RunStatus<String> {
        val result = areasRepository.edit(oldName, newName)
        if (result is RunStatus.Success) {
            localAreaImageRepository.rename(oldName, newName)
        }
        return result
    }
}