package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus

interface GeocodingRepositoryContract {
    suspend fun geocode(query: String): RunStatus<Pair<Double, Double>>
}
