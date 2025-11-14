package dev.seabat.ramennote.data.repository

interface UnsplashImageRepositoryContract {
    suspend fun fetch(query: String): ByteArray
}
