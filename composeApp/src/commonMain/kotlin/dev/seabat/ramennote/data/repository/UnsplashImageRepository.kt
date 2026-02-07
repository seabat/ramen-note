package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.config.UnsplashConfig
import dev.seabat.ramennote.domain.model.RunStatus
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

const val BASE_URL = "https://api.unsplash.com"

class UnsplashImageRepository(
    private val httpClient: HttpClient
) : UnsplashImageRepositoryContract {
    override suspend fun fetch(query: String): RunStatus<ByteArray> =
        withContext(Dispatchers.IO) {
            runCatching {
                httpClient
                    .get("$BASE_URL/search/photos") {
                        headers {
                            append("Authorization", "Client-ID ${UnsplashConfig.ACCESS_KEY}")
                        }
                        parameter("query", query)
                        parameter("page", 1)
                        parameter("per_page", 1)
                        parameter("orientation", "landscape")
                    }.body<UnsplashSearchResponse>()
            }.fold(
                onSuccess = { response: UnsplashSearchResponse ->
                    val firstPhoto = response.results.firstOrNull()
                    if (firstPhoto == null) {
                        RunStatus.Error("$query の画像が見つかりませんでした。")
                    } else {
                        val imageResponse = httpClient.get(firstPhoto.urls.regular)
                        RunStatus.Success(imageResponse.body<ByteArray>())
                    }
                },
                onFailure = { e -> RunStatus.Error(toErrorMessage(e)) }
            )
        }
}

private fun toErrorMessage(e: Throwable): String = when {
    e is HttpRequestTimeoutException -> "通信がタイムアウトしました"
    e.message?.contains("timeout", ignoreCase = true) == true -> "通信がタイムアウトしました"
    e.cause?.message?.contains("timeout", ignoreCase = true) == true -> "通信がタイムアウトしました"
    else -> e.message ?: "画像の取得に失敗しました"
}

@Serializable
data class UnsplashSearchResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
)

@Serializable
data class UnsplashPhoto(
    val id: String,
    val urls: UnsplashUrls,
    val user: UnsplashUser,
    val description: String? = null,
    val alt_description: String? = null
)

@Serializable
data class UnsplashUrls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)

@Serializable
data class UnsplashUser(
    val id: String,
    val username: String,
    val name: String
)
