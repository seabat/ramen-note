package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.domain.model.RunStatus

interface LocalImageRepositoryContract {
    suspend fun save(imageBytes: ByteArray, name: String)

    suspend fun load(name: String): RunStatus<ByteArray>

    suspend fun rename(oldName: String, newName: String)

    suspend fun delete(name: String)
}
