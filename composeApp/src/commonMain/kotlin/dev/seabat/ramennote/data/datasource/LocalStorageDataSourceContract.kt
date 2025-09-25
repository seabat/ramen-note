package dev.seabat.ramennote.data.datasource

interface LocalStorageDataSourceContract {
    suspend fun save(imageBytes: ByteArray)
    suspend fun load(): ByteArray?
}