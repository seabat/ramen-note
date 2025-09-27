package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class LoadImageListUseCase(
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : LoadImageListUseCaseContract {

    override suspend fun invoke(): RunStatus<List<ByteArray>> {
        return localAreaImageRepository.load()?.let { image ->
            RunStatus.Success(listOf(image))
        } ?: RunStatus.Success(listOf())
    }
}