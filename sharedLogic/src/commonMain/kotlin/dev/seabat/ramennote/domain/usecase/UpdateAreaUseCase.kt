package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class UpdateAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : UpdateAreaUseCaseContract {
    override suspend fun invoke(areaId: Int, newName: String): RunStatus<String> {
        val oldName =
            areasRepository.loadByAreaId(areaId)?.name
                ?: return RunStatus.Error("エリアが見つかりません")

        // 同じ名前のエリアがすでに登録されていないかチェック（自分自身は除く）
        if (oldName != newName && areasRepository.load(newName) != null) {
            return RunStatus.Error("すでに同じエリア名が登録されています")
        }

        val result = areasRepository.edit(oldName, newName)
        return if (result is RunStatus.Success) {
            try {
                // 画像名のリネーム
                localAreaImageRepository.rename(oldName, newName)

                // areaId が外部キーのため、エリア名変更時のShop更新は不要
                RunStatus.Success("")
            } catch (e: Exception) {
                RunStatus.Error("${e.message}")
            }
        } else {
            result
        }
    }
}
