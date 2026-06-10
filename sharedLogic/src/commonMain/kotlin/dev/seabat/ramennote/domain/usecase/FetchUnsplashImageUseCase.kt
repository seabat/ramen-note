package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.data.repository.UnsplashImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate

/**
 * Unsplash から画像を取得する（ローカル保存は行わない）
 *
 * ローカル画像が存在しない場合は制限なく取得する。
 * ローカル画像が存在する場合は updatedDate が今日より前であれば取得を許可し、
 * 今日と同じ場合はエラーを返す。
 */
class FetchUnsplashImageUseCase(
    private val unsplashImageRepository: UnsplashImageRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val areasRepository: AreasRepositoryContract
) : FetchUnsplashImageUseCaseContract {
    override suspend operator fun invoke(areaName: String): RunStatus<ByteArray> {
        when (val runStatus = localAreaImageRepository.load(areaName)) {
            is RunStatus.Error if (runStatus.data == null) -> {
                return unsplashImageRepository.fetch(areaName)
            }
            else -> {}
        }

        val areaData =
            areasRepository.load(areaName)
                ?: return RunStatus.Error("エリアが登録されていません")

        val today = createTodayLocalDate()
        return if (areaData.updatedDate < today) {
            unsplashImageRepository.fetch(areaName)
        } else {
            RunStatus.Error("本日は画像を変更できません。明日もう一度お試しください。")
        }
    }
}
