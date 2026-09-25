import sharedUI
import Foundation
import FirebaseAILogic

class IosShopAiDataSource: ShopAiDataSourceContract {
    func __generate(prompt: String) async throws -> ShopAiInfo {
        // Android 側と同等の JSON スキーマを定義
        let jsonSchema = Schema.object(
            properties: [
                "shopName": .string(),
                "shopUrl": .string(),
                "mapUrl": .string(),
                "stationName": .string(),
                "category": .enumeration(
                    values: [
                        "醤油",
                        "味噌",
                        "塩",
                        "豚骨",
                        "豚骨醤油",
                        "家系",
                        "二郎系",
                        "つけ麺",
                        "その他"
                    ]
                ),
                "description": .string()
            ]
        )

        // googleAI() バックエンドは Firebase プロジェクトのプリペイドクレジットが枯渇すると利用不可になる。
        // Vertex AI バックエンドは Google Cloud の従量課金で動作しクレジット枯渇の影響を受けないため切り替えた。
        // gemini-2.5 系は 2026/10 に廃止されるため、より安価な gemini-3.1-flash-lite へ移行（Android と同期）。
        // Gemini 3.x は Firebase AI Logic では location="global" のみ対応のため明示指定する。
        // この SDK バージョンには Android の agentPlatform() に相当する API がまだ無いため vertexAI() のまま。
        let ai = FirebaseAI.firebaseAI(backend: .vertexAI(location: "global"))
        let model = ai.generativeModel(
            modelName: "gemini-3.1-flash-lite",
            generationConfig: GenerationConfig(
                maxOutputTokens: 512,
                responseMIMEType: "application/json",
                responseSchema: jsonSchema,
                // 思考トークンの課金を抑えるため思考を無効化する（Android と同期）。
                thinkingConfig: ThinkingConfig(thinkingBudget: 0)
            )
        )

        let response = try await model.generateContent(prompt)
        guard let jsonText = response.text, !jsonText.isEmpty else {
            return ShopAiInfo(
                shopName: "",
                shopUrl: "",
                mapUrl: "",
                stationName: "",
                category: "",
                description: ""
            )
        }

        // JSON をデコードして ShopAiInfo に変換
        struct ShopAiInfoDTO: Decodable {
            let shopName: String?
            let shopUrl: String?
            let mapUrl: String?
            let stationName: String?
            let category: String?
            let description: String?
        }

        do {
            let data = Data(jsonText.utf8)
            let dto = try JSONDecoder().decode(ShopAiInfoDTO.self, from: data)
            return ShopAiInfo(
                shopName: dto.shopName ?? "",
                shopUrl: dto.shopUrl ?? "",
                mapUrl: dto.mapUrl ?? "",
                stationName: dto.stationName ?? "",
                category: dto.category ?? "",
                description: dto.description ?? ""
            )
        } catch {
            // デコードに失敗した場合は空の ShopAiInfo を返す（Android 側の getOrElse と同じ）
            return ShopAiInfo(
                shopName: "",
                shopUrl: "",
                mapUrl: "",
                stationName: "",
                category: "",
                description: ""
            )
        }
    }
}
