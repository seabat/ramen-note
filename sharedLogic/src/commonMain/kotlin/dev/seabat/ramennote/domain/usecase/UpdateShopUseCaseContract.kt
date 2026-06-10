package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.domain.model.Shop

interface UpdateShopUseCaseContract {
    suspend operator fun invoke(shop: Shop, byteArray: ByteArray?, oldAreaId: Int)
}
