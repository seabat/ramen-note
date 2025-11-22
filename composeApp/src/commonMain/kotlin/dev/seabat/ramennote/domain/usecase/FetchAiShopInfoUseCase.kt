package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.ShopAiRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.ShopAiInfo

class FetchAiShopInfoUseCase(
    private val shopAiRepository: ShopAiRepositoryContract
) : FetchAiShopUseCaseContract {
    override suspend operator fun invoke(shopName: String) : RunStatus<ShopAiInfo> {
        return shopAiRepository.fetch(shopName)
    }
}