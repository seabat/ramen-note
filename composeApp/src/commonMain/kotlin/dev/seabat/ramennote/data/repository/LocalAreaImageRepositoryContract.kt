package dev.seabat.ramennote.data.repository

interface LocalAreaImageRepositoryContract {
    suspend fun save(imageBytes: ByteArray)
    suspend fun load(): ByteArray?
}
