package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate

/**
 * エリア画像を更新する
 *
 * ローカルに画像が存在しない場合は Unsplash から取得して保存する。
 * ローカルに画像が存在する場合は、最終更新日が今日より前であれば Unsplash から再取得して更新する。
 * 最終更新日が今日の場合はエラーを返す。
 */
class UpdateAreaImageUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract,
    private val fetchAndSaveUnsplashImageUseCase: FetchAndSaveUnsplashImageUseCaseContract
) : UpdateAreaImageUseCaseContract {
    override suspend operator fun invoke(areaName: String): RunStatus<ByteArray> {

        // まずローカルから画像を読み込む。画像がない場合は必ず Unsplash から取得
        when (val runStatus = localAreaImageRepository.load(areaName)) {
            is RunStatus.Error if (runStatus.data == null) -> {
                return fetchAndSaveUnsplashImageUseCase(areaName)
            }
            else -> {}
        }
        val areaData =
            areasRepository.load(areaName)
                ?: return RunStatus.Error("エリアが登録されていません")

        val today = createTodayLocalDate()
        val needUpdate = areaData.updatedDate < today
        return if (needUpdate) {
            fetchAndSaveUnsplashImageUseCase(areaName)
        } else {
            RunStatus.Error("本日は画像を変更できません。明日もう一度お試しください。")
        }
    }
}
