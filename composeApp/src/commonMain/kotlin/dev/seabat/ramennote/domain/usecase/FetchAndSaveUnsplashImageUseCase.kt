package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

/**
 * Unsplash から画像を取得し、ローカルストレージに保存する
 *
 *
 * @property unsplashImageRepository
 * @property localAreaImageRepository
 */
class FetchAndSaveUnsplashImageUseCase(
    private val unsplashImageRepository: UnsplashImageRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : FetchAndSaveUnsplashImageUseCaseContract {

    override suspend operator fun invoke(query: String): RunStatus<ByteArray> {
        // Fetch image from remote repository
        return when(val fetchResult = unsplashImageRepository.fetch(query)) {
            is RunStatus.Error -> {
                return fetchResult
            }
            is RunStatus.Success -> {
                val imageBytes = requireNotNull(fetchResult.data) { "Success の data は null でない想定" }
                // Save to local storage with query as filename
                localAreaImageRepository.save(imageBytes, query)

                // Load from local storage with query as filename
                when(val localImageBytes = localAreaImageRepository.load(query)) {
                    is RunStatus.Success -> localImageBytes
                    is RunStatus.Error -> RunStatus.Error("Failed to load image from local storage")
                    else -> error("unexpected state")
                }
            }
            else -> {
               error("ここは通らない")
            }
        }
    }
}
