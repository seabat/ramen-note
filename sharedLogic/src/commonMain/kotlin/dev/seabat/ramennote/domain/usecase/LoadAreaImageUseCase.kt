package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.LocalImageRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus

class LoadAreaImageUseCase(
    private val areasRepository: AreasRepositoryContract,
    private val localAreaImageRepository: LocalImageRepositoryContract
) : LoadAreaImageUseCaseContract {
    override suspend operator fun invoke(areaId: Int): RunStatus<ByteArray> {
        val areaName =
            areasRepository.loadByAreaId(areaId)?.name
                ?: return RunStatus.Error("エリアが見つかりません")
        return localAreaImageRepository.load(areaName)
    }
}
