package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.Area
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.util.createTodayLocalDate

class AddAreaUseCase(
    private val areasRepository: AreasRepositoryContract
) : AddAreaUseCaseContract {
    override suspend operator fun invoke(areaName: String): RunStatus<String> {
        val trimmedName = areaName.trim()

        // 同じ名前のエリアがすでに登録されていないかチェック
        if (areasRepository.load(trimmedName) != null) {
            return RunStatus.Error("すでに同じエリア名が登録されています")
        }

        areasRepository.add(
            Area(
                name = trimmedName,
                updatedDate = createTodayLocalDate(),
                count = 0,
                sort = 0
            )
        )
        return RunStatus.Success("")
    }
}
