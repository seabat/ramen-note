package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class FetchUnsplashImageUseCase(
    private val unsplashImageRepository: UnsplashImageRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
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
            RunStatus.Error("画像の生成に失敗しました")
        }
    }
}