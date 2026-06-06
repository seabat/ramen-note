package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopsRepositoryContract
import dev.seabat.ramennote.domain.model.Shop

class SearchShopsByNameUseCase(
    private val shopsRepository: ShopsRepositoryContract
) : SearchShopsByNameUseCaseContract {
    override suspend fun invoke(query: String): List<Shop> = shopsRepository.getShopsByName(query)
}
