package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class FetchImageUseCase(
    private val areaImageRepository: AreaImageRepositoryContract,
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : FetchImageUseCaseContract {
    override suspend operator fun invoke(): RunStatus<ByteArray?> {
        return try {
            // Fetch image from remote repository
            val imageBytes = areaImageRepository.fetch()
            
            // Save to local storage
            localAreaImageRepository.save(imageBytes)
            
            // Load from local storage
            val localImageBytes = localAreaImageRepository.load()
            
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