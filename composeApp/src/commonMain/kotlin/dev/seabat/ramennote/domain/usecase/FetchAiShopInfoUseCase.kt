package dev.seabat.ramennote.domain.usecase

import dev.seabat.ramennote.data.repository.GoogleMapSearchUrlRepositoryContract
import dev.seabat.ramennote.data.repository.ShopAiRepositoryContract
import dev.seabat.ramennote.domain.model.RunStatus
import dev.seabat.ramennote.domain.model.ShopAiInfo

class FetchAiShopInfoUseCase(
    private val shopAiRepository: ShopAiRepositoryContract,
    private val googleMapSearchUrlRepository: GoogleMapSearchUrlRepositoryContract
) : FetchAiShopUseCaseContract {
    override suspend operator fun invoke(areaName: String, shopName: String): RunStatus<ShopAiInfo> {
        val prompt =
            """
            以下のラーメン店の情報を調べて、JSON形式で返してください：
            店舗の所在する地域: $areaName
            店舗名: $shopName
            
            以下の情報を含めてください：
            - shopName: 店舗名
            - shopUrl: 店舗の公式WebサイトURL（なければ空文字）
            - mapUrl: 空文字（""）を返してください。このフィールドは使用しません。
            - stationName: 最寄り駅名
            - category: ラーメンのカテゴリ。以下の9つのカテゴリから最も適切なものを1つ選択してください。店舗のメニュー、特徴、レビュー、公式サイトなどの情報を参考に、できる限り正確に分類してください。「その他」は上記の8つのカテゴリに該当しない場合のみ使用してください。
              1. 醤油: 醤油ベースのスープが特徴。透明感のある茶色いスープ。
              2. 味噌: 味噌ベースのスープが特徴。濃厚でコクがある。
              3. 塩: 塩ベースのスープが特徴。あっさりとした透明なスープ。
              4. 豚骨: 豚骨ベースのスープが特徴。白濁した濃厚なスープ。
              5. 豚骨醤油: 豚骨と醤油を合わせたスープが特徴。
              6. 家系: 横浜家系ラーメンのスタイル。濃厚な豚骨醤油スープ、太めのストレート麺、ほうれん草、のりが特徴。
              7. 二郎系: 東京の「ラーメン二郎」スタイル。大量の野菜（もやし、キャベツ）、ニンニク、背脂が特徴。
              8. つけ麺: つけ麺専門店。麺とスープが別々で提供される。
              9. その他: 上記の8つのカテゴリに明確に該当しない場合のみ使用。
            """.trimIndent()

        // AIから取得した情報に基づいて、Googleマップの検索URLを生成
        return when (val result = shopAiRepository.fetch(prompt)) {
            is RunStatus.Success -> {
                val shopAiInfo = result.data
                // GoogleマップURLを生成
                val mapUrl =
                    when (val mapUrlResult = googleMapSearchUrlRepository.createUrl(areaName, shopName)) {
                        is RunStatus.Success -> mapUrlResult.data ?: ""
                        else -> ""
                    }

                RunStatus.Success(
                    shopAiInfo?.copy(mapUrl = mapUrl) ?: ShopAiInfo(shopName = shopName)
                )
            }
            else -> result
        }
    }
}
