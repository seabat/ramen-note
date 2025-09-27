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

            // Save to local storage with query as filename
            localAreaImageRepository.save(imageBytes, query)

            // Load from local storage with query as filename
            val localImageBytes = localAreaImageRepository.load(query)

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