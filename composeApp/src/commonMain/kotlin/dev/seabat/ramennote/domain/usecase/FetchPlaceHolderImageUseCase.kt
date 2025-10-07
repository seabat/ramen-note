package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class FetchPlaceHolderImageUseCase(
    private val areaImageRepository: AreaImageRepositoryContract,
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : FetchPlaceHolderImageUseCaseContract {
    override suspend operator fun invoke(): RunStatus<ByteArray?> {
        return try {
            // Fetch image from remote repository
            val imageBytes = areaImageRepository.fetch()
            
            // Save to local storage with default filename
            localAreaImageRepository.save(imageBytes, "area_image")
            
            // Load from local storage with default filename
            val localImageBytes = localAreaImageRepository.load("area_image")
            
            if (localImageBytes != null) {
                RunStatus.Success(localImageBytes)
            } else {
                RunStatus.Error("Failed to load image from local storage")
            }
        } catch (e: Exception) {
            RunStatus.Error("Failed to fetch image: ${e.message}")
        }
    }
}