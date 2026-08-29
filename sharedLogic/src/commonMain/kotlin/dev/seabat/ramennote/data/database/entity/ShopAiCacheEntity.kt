package dev.seabat.ramennote.data.database.entity

import androidx.room.Entity

/**
 * AI 生成した店情報のキャッシュ。
 *
 * 同一 (areaName, shopName) の生成結果を保持し、再生成時に Vertex AI を再度呼ばずに
 * キャッシュを返すことで API コストを削減する目的で導入した。
 *
 * @property areaName 検索キー: エリア名（ユーザー入力）
 * @property shopName 検索キー: 店名（ユーザー入力）
 * @property shopUrl 店舗の公式 Web サイト URL
 * @property mapUrl Google マップ URL（AI は空文字を返すため実質未使用。UseCase 側で毎回再生成する）
 * @property stationName 最寄り駅名
 * @property category ラーメンのカテゴリ
 * @property description 店舗の紹介文
 * @property createdAt キャッシュ生成日（ISO8601 の日付文字列。将来の TTL / 手動再生成の判断用）
 */
@Entity(
    tableName = "shop_ai_cache",
    primaryKeys = ["areaName", "shopName"]
)
data class ShopAiCacheEntity(
    val areaName: String,
    val shopName: String,
    val shopUrl: String,
    val mapUrl: String,
    val stationName: String,
    val category: String,
    val description: String,
    val createdAt: String
)
