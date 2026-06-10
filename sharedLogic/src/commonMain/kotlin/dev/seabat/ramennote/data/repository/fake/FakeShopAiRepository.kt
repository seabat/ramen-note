package dev.seabat.ramennote.data.repository.fake

import dev.seabat.ramennote.data.repository.ShopAiRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.ShopAiInfo

class FakeShopAiRepository : ShopAiRepositoryContract {
    override suspend fun fetch(prompt: String): RunStatus<ShopAiInfo> =
        RunStatus.Success(
            ShopAiInfo(
                shopName = "〇〇家",
                shopUrl = "https://example-ramen-shop.com",
                mapUrl = "https://maps.google.com/?q=〇〇家",
                stationName = "新宿駅",
                category = "醤油"
            )
        )
}
