package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop
import dev.seabat.ramennote.domain.model.ShopLocation

interface ResolveShopLocationUseCaseContract {
    suspend operator fun invoke(shop: Shop): ShopLocation?
}
