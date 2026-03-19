package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class FetchPlaceHolderImageUseCase(
    private val areaImageRepository: AreaImageRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : FetchPlaceHolderImageUseCaseContract {
    override suspend operator fun invoke(): RunStatus<ByteArray> =
        try {
            // Fetch image from remote repository
            val imageBytes = areaImageRepository.fetch()

            // Save to local storage with default filename
            localAreaImageRepository.save(imageBytes, "area_image")

            // Load from local storage with default filename
            when (val localImageBytes = localAreaImageRepository.load("area_image")) {
                is RunStatus.Success -> localImageBytes
                is RunStatus.Error -> RunStatus.Error("Failed to load image from local storage")
                else -> error("Unexpected state")
            }
        } catch (e: Exception) {
            RunStatus.Error("Failed to fetch image: ${e.message}")
        }
}
