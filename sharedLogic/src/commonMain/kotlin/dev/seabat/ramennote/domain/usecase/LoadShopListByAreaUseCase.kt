package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.AreasRepositoryContract
import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

/**
 * エリア別の店舗一覧を取得するユースケース
 *
 * - Shop.star の降順に並べる
 *
 * @property shopsRepository
 * @property areasRepository
 */
class LoadShopListByAreaUseCase(
    private val shopsRepository: ShopsRepositoryContract,
    private val areasRepository: AreasRepositoryContract
) : LoadShopListByAreaUseCaseContract {
    override suspend operator fun invoke(area: String): List<Shop> {
        val areaEntity = areasRepository.load(area) ?: return emptyList()
        return shopsRepository.getShopsByAreaId(areaEntity.areaId).sortedByDescending { it.star }
    }
}
