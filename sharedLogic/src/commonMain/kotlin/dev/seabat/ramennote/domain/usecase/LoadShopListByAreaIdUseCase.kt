package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

class LoadShopListByAreaIdUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : LoadShopListByAreaIdUseCaseContract {
    override suspend operator fun invoke(areaId: Int): List<Shop> =
        shopsRepository.getShopsByAreaId(areaId).sortedByDescending { it.star }
}
