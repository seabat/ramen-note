package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface SearchShopsByNameUseCaseContract {
    suspend operator fun invoke(query: String): List<Shop>
}
