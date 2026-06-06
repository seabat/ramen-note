package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class DeleteAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : DeleteAreaUseCaseContract {
    override suspend operator fun invoke(areaId: Int): RunStatus<String> {
        // 削除前に エリアを取得（削除後は loadByAreaId() が null を返すため）
        val area =
            areasRepository.loadByAreaId(areaId)
                ?: return RunStatus.Error("エリアが見つかりません")

        if (area.count >= 1) {
            return RunStatus.Error("エリアを削除する前にエリアに登録しているお店を全て削除してください。")
        }

        val name = area.name

        val result = areasRepository.delete(name)
        return if (result is RunStatus.Success) {
            try {
                // エリア画像の削除
                localAreaImageRepository.delete(name)
                RunStatus.Success("")
            } catch (e: Exception) {
                RunStatus.Error(e.message ?: "エリア削除後の関連データ削除に失敗しました")
            }
        } else {
            result
        }
    }
}
