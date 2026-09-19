package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.ShopAiInfo

/**
 * AI 生成した店情報のキャッシュを扱う Repository の Contract。
 *
 * 同一 (areaName, shopName) の生成結果を再利用し、Vertex AI の再呼び出し（＝課金）を避ける。
 */
interface ShopAiCacheRepositoryContract {
    /** キャッシュを取得する。存在しなければ null。 */
    suspend fun get(areaName: String, shopName: String): ShopAiInfo?

    /** 生成結果をキャッシュに保存する（既存があれば上書き）。 */
    suspend fun save(areaName: String, shopName: String, info: ShopAiInfo)
}
