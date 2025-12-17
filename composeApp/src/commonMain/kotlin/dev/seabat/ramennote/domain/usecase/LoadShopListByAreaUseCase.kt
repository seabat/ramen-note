package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

/**
 * エリア別の店舗一覧を取得するユースケース
 *
 * - Shop.star の降順に並べる
 *
 * @property shopsRepository
 */
class LoadShopListByAreaUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : LoadShopListByAreaUseCaseContract {
    override suspend operator fun invoke(area: String): List<Shop> =
        shopsRepository.getShopsByArea(area).sortedByDescending { it.star }
}
