package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus

interface UnsplashImageRepositoryContract {
    suspend fun fetch(query: String): RunStatus<ByteArray>
}
