package dev.seabat.ramennote.data.datasource

interface LocalStorageDataSourceContract {
    suspend fun save(imageBytes: ByteArray, name: String)
    suspend fun load(name: String): ByteArray?
    suspend fun rename(oldName: String, newName: String)

    suspend fun delete(name: String)
}