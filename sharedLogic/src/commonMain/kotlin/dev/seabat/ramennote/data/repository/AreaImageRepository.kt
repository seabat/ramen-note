package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.repository.AreaImageRepositoryContract
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * エリア画像を placehold.jp から取得する
 *
 * @property httpClient
 */
class AreaImageRepository(
    private val httpClient: HttpClient
) : AreaImageRepositoryContract {
    override suspend fun fetch(): ByteArray = httpClient.get("https://placehold.jp/800x500.png").body()
}
