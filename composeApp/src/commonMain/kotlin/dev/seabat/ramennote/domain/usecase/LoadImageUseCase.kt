package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class LoadImageUseCase(
    private val localImageRepository: LocalImageRepositoryContract
) : LoadImageUseCaseContract {
    override suspend operator fun invoke(name: String): RunStatus<ByteArray?> = RunStatus.Success(localImageRepository.load(name))
}
