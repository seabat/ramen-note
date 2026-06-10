package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders

class ExpandShortUrlRepository(
    private val httpClient: HttpClient
) : ExpandShortUrlRepositoryContract {
    // HttpRedirect プラグインが Ktor 層でリダイレクトを追跡するため、
    // 最終リダイレクト先の URL は response.request.url から取得できる
    override suspend fun expand(shortUrl: String): RunStatus<String> =
        try {
            val response = httpClient.get(shortUrl) {
                headers {
                    append(HttpHeaders.UserAgent, "Mozilla/5.0")
                }
            }
            RunStatus.Success(response.call.request.url.toString())
        } catch (e: Exception) {
            RunStatus.Error("URL 展開エラー: ${e.message}")
        }
}
