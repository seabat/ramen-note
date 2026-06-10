package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface LoadShopListByAreaIdUseCaseContract {
    suspend operator fun invoke(areaId: Int): List<Shop>
}
