package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate

class UpdateAreaUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : UpdateAreaUseCaseContract {
    override suspend fun invoke(areaId: Int, newName: String, byteArray: ByteArray?): RunStatus<String> {
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
                if (byteArray != null) {
                    // 新画像を newName で保存し updatedDate を更新
                    localAreaImageRepository.save(byteArray, newName)
                    areasRepository.load(newName)?.let { area ->
                        areasRepository.edit(area.copy(updatedDate = createTodayLocalDate()))
                    }
                    // 名前が変わった場合は旧ファイルを削除
                    if (oldName != newName) {
                        localAreaImageRepository.delete(oldName)
                    }
                } else if (oldName != newName) {
                    // 画像変更なし・名前変更あり: ファイルをリネーム
                    localAreaImageRepository.rename(oldName, newName)
                }
                RunStatus.Success("")
            } catch (e: Exception) {
                RunStatus.Error("${e.message}")
            }
        } else {
            result
        }
    }
}
