package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders

class ExpandShortUrlRepository(
    private val httpClient: HttpClient
) : ExpandShortUrlRepositoryContract {
    override suspend fun expand(shortUrl: String): RunStatus<String> =
        try {
            var currentUrl = shortUrl
            // maps.app.goo.gl は複数回リダイレクトする場合があるため最大10回追跡する
            repeat(10) {
                val response = httpClient.get(currentUrl) {
                    headers {
                        append(HttpHeaders.UserAgent, "Mozilla/5.0")
                    }
                }
                val location = response.headers[HttpHeaders.Location]
                if (response.status.value in 300..399 && location != null) {
                    currentUrl = location
                } else {
                    return RunStatus.Success(currentUrl)
                }
            }
            RunStatus.Error("リダイレクト上限超過")
        } catch (e: Exception) {
            RunStatus.Error("URL 展開エラー: ${e.message}")
        }
}
