package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate

/**
 * Unsplash から画像を取得し、ローカルストレージに保存する
 *
 * 画像取得に成功した場合は、areas テーブルの該当レコードの updatedDate を本日の日付で更新する。
 *
 * @property unsplashImageRepository
 * @property localAreaImageRepository
 * @property areasRepository
 */
class FetchAndSaveUnsplashImageUseCase(
    private val unsplashImageRepository: UnsplashImageRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val areasRepository: AreasRepositoryContract
) : FetchAndSaveUnsplashImageUseCaseContract {
    override suspend operator fun invoke(query: String): RunStatus<ByteArray> {
        // Fetch image from remote repository
        return when (val fetchResult = unsplashImageRepository.fetch(query)) {
            is RunStatus.Error -> {
                return fetchResult
            }
            is RunStatus.Success -> {
                val imageBytes = requireNotNull(fetchResult.data) { "Success の data は null でない想定" }
                // Save to local storage with query as filename
                localAreaImageRepository.save(imageBytes, query)

                // areas テーブルの updatedDate を本日の日付で更新する
                areasRepository.load(query)?.let { area ->
                    areasRepository.edit(area.copy(updatedDate = createTodayLocalDate()))
                }

                // Load from local storage with query as filename
                when (val runStatus = localAreaImageRepository.load(query)) {
                    is RunStatus.Success -> runStatus
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
