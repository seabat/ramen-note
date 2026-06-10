package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract

class LoadImagePathUseCase(
    private val localImageRepository: LocalImageRepositoryContract
) : LoadImagePathUseCaseContract {
    override suspend operator fun invoke(photoName: String): String? = localImageRepository.getFilePath(photoName)
}
