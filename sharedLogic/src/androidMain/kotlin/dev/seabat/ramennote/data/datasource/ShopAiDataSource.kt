package dev.seabat.ramennote.data.datasource

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import dev.seabat.ramennote.domain.model.ShopAiInfo
import kotlinx.serialization.json.Json

class ShopAiDataSource : ShopAiDataSourceContract {
    override suspend fun generate(prompt: String): ShopAiInfo {
        val jsonSchema =
            Schema.obj(
                mapOf(
                    "shopName" to Schema.string(),
                    "shopUrl" to Schema.string(),
                    "mapUrl" to Schema.string(),
                    "stationName" to Schema.string(),
                    "category" to Schema.enumeration(listOf("醤油", "味噌", "塩", "豚骨", "豚骨醤油", "家系", "二郎系", "つけ麺", "その他")),
                    "description" to Schema.string()
                )
            )

        // googleAI() バックエンドは Firebase プロジェクト単位のプリペイドクレジットが枯渇すると利用不可になる。
        // Vertex AI バックエンドは Google Cloud の従量課金で動作し、クレジット枯渇の影響を受けないため切り替えた。
        // gemini-2.5 系は 2026/10 に廃止されるため、より安価な gemini-3.1-flash-lite へ移行済み。
        // Gemini 3.x は Firebase AI Logic では location="global" のみ対応のため明示指定する。
        val model =
            Firebase.ai(backend = GenerativeBackend.vertexAI(location = "global")).generativeModel(
                modelName = "gemini-3.1-flash-lite",
                generationConfig =
                    generationConfig {
                        responseMimeType = "application/json"
                        responseSchema = jsonSchema
                        // 思考トークンの課金を抑えるため思考を無効化する。
                        // 店情報の抽出・分類は単純タスクで内部推論を要さず、思考トークンは
                        // 出力単価で課金されるため、ここが最大のコスト削減ポイントとなる。
                        thinkingConfig =
                            thinkingConfig {
                                thinkingBudget = 0
                            }
                        // description の長文化による出力トークン増を上限で抑制する。
                        // 途中で JSON が切れて parse 失敗しないよう余裕を持たせた値にする。
                        maxOutputTokens = 512
                    }
            )
        val response = model.generateContent(prompt)
        val jsonText = response.text ?: return ShopAiInfo()

        return runCatching {
            Json { ignoreUnknownKeys = true }
                .decodeFromString(ShopAiInfo.serializer(), jsonText)
        }.getOrElse { ShopAiInfo() }
    }
}
