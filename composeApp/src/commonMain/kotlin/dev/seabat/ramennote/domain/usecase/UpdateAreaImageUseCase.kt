package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate
import kotlinx.datetime.minus

class UpdateAreaImageUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val fetchAndSaveUnsplashImageUseCaseContract: FetchAndSaveUnsplashImageUseCaseContract
) : UpdateAreaImageUseCaseContract {
    override suspend operator fun invoke(areaId: Int): RunStatus<ByteArray> {
        val areaData =
            areasRepository.loadByAreaId(areaId)
                ?: return RunStatus.Error("エリアが見つかりません")
        val areaName = areaData.name

        // まずローカルから画像を読み込む。null なら必ず Unsplash から取得
        localAreaImageRepository.load(areaName) ?: return fetchAndSaveUnsplashImageUseCaseContract(areaName)

        val today = createTodayLocalDate()
        val needUpdate = areaData.updatedDate < today.minus(1, kotlinx.datetime.DateTimeUnit.DAY)
        return if (needUpdate) {
            fetchAndSaveUnsplashImageUseCaseContract(areaName)
        } else {
            RunStatus.Error("本日は画像を変更できません。明日もう一度お試しください。")
        }
    }
}
