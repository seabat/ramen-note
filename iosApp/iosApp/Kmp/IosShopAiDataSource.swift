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

        let ai = FirebaseAI.firebaseAI(backend: .googleAI())
        let model = ai.generativeModel(
            modelName: "gemini-2.5-flash",
            generationConfig: GenerationConfig(
                responseMIMEType: "application/json",
                responseSchema: jsonSchema
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
