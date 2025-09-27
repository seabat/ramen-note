package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.config.UnsplashConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class UnsplashImageRepository(
    private val httpClient: HttpClient
) : UnsplashImageRepositoryContract {

    val BASE_URL = "https://api.unsplash.com"

    override suspend fun fetch(query: String): ByteArray {
        return withContext(Dispatchers.IO) {
            val response:UnsplashSearchResponse = httpClient.get("$BASE_URL/search/photos") {
                headers {
                    append("Authorization", "Client-ID ${UnsplashConfig.ACCESS_KEY}")
                }
                parameter("query", query)
                parameter("page", 1)
                parameter("per_page", 1)
                parameter("orientation", "landscape")
            }.body()

            // 画像URLから画像データを取得
            val imageResponse = httpClient.get(response.results.get(0).urls.regular)
            imageResponse.body<ByteArray>()
        }
    }
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
