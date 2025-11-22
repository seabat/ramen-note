package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.ShopAiInfo

class ShopAiRepository : ShopAiRepositoryContract {
    override suspend fun fetch(shopName: String) : RunStatus<ShopAiInfo> {
        return try {
            // TODO: Gemini APIの実装
            // 1. Gemini APIに店舗名を送信
            // 2. ラーメン店の情報を取得
            // 3. WebサイトURL、GoogleマップURL、最寄り駅、カテゴリを抽出
            // 4. ShopInfoオブジェクトに変換

            val prompt = """
                以下のラーメン店の情報を調べて、JSON形式で返してください：
                店舗名: $shopName
                
                以下の情報を含めてください：
                - shopUrl: 店舗の公式WebサイトURL（なければ空文字）
                - mapUrl: GoogleマップのURL
                - stationName: 最寄り駅名
                - category: ラーメンのカテゴリ（醤油、味噌、塩、豚骨、豚骨醤油、家系、二郎系、つけ麺、その他）
            """.trimIndent()

            // TODO: 実際のGemini API呼び出し
            val mockResponse = ShopAiInfo(
                shopName = shopName,
                shopUrl = "https://example-ramen-shop.com",
                mapUrl = "https://maps.google.com/?q=${shopName}",
                stationName = "新宿駅",
                category = "醤油"
            )

            RunStatus.Success(mockResponse)
        } catch (e: Exception) {
            RunStatus.Error(e.message ?: "")
        }
    }
}