package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate
import kotlinx.datetime.minus

class UpdateAreaImageUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val fetchUnsplashImageUseCase: FetchUnsplashImageUseCaseContract
) : UpdateAreaImageUseCaseContract {
    override suspend operator fun invoke(area: String): RunStatus<ByteArray?> {
        // まずローカルから読み込む。nullなら必ずUnsplashから取得
        val local = localAreaImageRepository.load(area)
        if (local == null) {
            return fetchUnsplashImageUseCase(area)
        }

        val areaData = areasRepository.fetch(area)
        val today = createTodayLocalDate()
        val needUpdate = areaData == null || areaData.updatedDate < today.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        return if (needUpdate) {
            fetchUnsplashImageUseCase(area)
        } else {
            RunStatus.Error("本日は画像を変更できません。明日もう一度お試しください。")
        }
    }
}
