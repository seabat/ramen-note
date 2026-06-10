package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus
import io.ktor.http.URLBuilder

class GoogleMapSearchUrlRepository : GoogleMapSearchUrlRepositoryContract {
    override suspend fun createUrl(areaName: String, shopName: String): RunStatus<String> =
        try {
            val query = "$shopName $areaName".trim()

            // KtorのURLBuilderを使ってURLエンコード
            val mapUrl =
                URLBuilder("https://www.google.com/maps/search/")
                    .apply {
                        parameters.append("api", "1")
                        parameters.append("query", query)
                    }.build()
                    .toString()

            RunStatus.Success(mapUrl)
        } catch (e: Exception) {
            RunStatus.Error("GoogleマップURLの生成に失敗しました: ${e.message}")
        }
}
