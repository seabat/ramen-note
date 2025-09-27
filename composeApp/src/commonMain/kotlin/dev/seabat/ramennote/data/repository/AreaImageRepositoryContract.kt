package dev.seabat.ramennote.data.repository

interface AreaImageRepositoryContract {
    suspend fun fetch(): ByteArray
}