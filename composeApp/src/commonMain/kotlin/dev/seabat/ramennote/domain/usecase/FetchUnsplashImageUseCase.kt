package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalAreaImageRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class FetchUnsplashImageUseCase(
    private val unsplashImageRepository: UnsplashImageRepositoryContract,
    private val localAreaImageRepository: LocalAreaImageRepositoryContract
) : FetchUnsplashImageUseCaseContract {
    override suspend operator fun invoke(query: String): RunStatus<ByteArray?> {
        return try {
            // Fetch image from remote repository
            val imageBytes = unsplashImageRepository.fetch(query)

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