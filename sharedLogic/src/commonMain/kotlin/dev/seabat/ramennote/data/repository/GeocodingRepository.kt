package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.config.BuildSecrets
import dev.seabat.ramennote.domain.model.RunStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class GeocodingRepository(
    private val httpClient: HttpClient
) : GeocodingRepositoryContract {
    override suspend fun geocode(query: String): RunStatus<Pair<Double, Double>> =
        try {
            val response: GeocodingResponse =
                httpClient
                    .get("https://maps.googleapis.com/maps/api/geocode/json") {
                        parameter("address", query)
                        parameter("key", BuildSecrets.GOOGLE_MAPS_API_KEY)
                    }.body()

            if (response.status == "OK" && response.results.isNotEmpty()) {
                val location = response.results[0].geometry.location
                RunStatus.Success(Pair(location.lat, location.lng))
            } else {
                RunStatus.Error("Geocoding 失敗: status=${response.status}")
            }
        } catch (e: Exception) {
            RunStatus.Error("Geocoding エラー: ${e.message}")
        }
}

@Serializable
private data class GeocodingResponse(
    val status: String,
    val results: List<GeocodingResult>
)

@Serializable
private data class GeocodingResult(
    val geometry: GeocodingGeometry
)

@Serializable
private data class GeocodingGeometry(
    val location: GeocodingLocation
)

@Serializable
private data class GeocodingLocation(
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double
)
