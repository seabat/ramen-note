package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class LoadImageUseCase(
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : LoadImageUseCaseContract {

    override suspend operator fun invoke(): RunStatus<ByteArray?> {
        return RunStatus.Success(localAreaImageRepository.load())
    }
}