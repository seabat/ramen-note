package dev.seabat.ramennote.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class AreaImageRepository(
    private val httpClient: HttpClient
) : AreaImageRepositoryContract {
    override suspend fun fetch(): ByteArray {
        return httpClient.get("https://placehold.jp/800x500.png").body()
    }
}


