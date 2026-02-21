package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.domain.model.Area

/**
 * エリア一覧を取得するユースケース
 *
 * - Area.sort の降順に並べる
 *
 * @property areasRepository
 */
class LoadAreasUseCase(
    private val areasRepository: AreasRepositoryContract
) : LoadAreasUseCaseContract {
    override suspend operator fun invoke(): List<Area> = areasRepository.load().sortedByDescending { it.sort }
}
