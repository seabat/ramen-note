package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LocalAreaImageRepository(
    private val localStorageDataSource: LocalStorageDataSourceContract
) : LocalAreaImageRepositoryContract {
    
    override suspend fun save(imageBytes: ByteArray, name: String) {
        withContext(Dispatchers.IO) {
            localStorageDataSource.save(imageBytes, name)
        }
    }
    
    override suspend fun load(name: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            localStorageDataSource.load(name)
        }
    }

    override suspend fun rename(oldName: String, newName: String) {
        withContext(Dispatchers.IO) {
            localStorageDataSource.rename(oldName, newName)
        }
    }

    override suspend fun delete(name: String) {
        withContext(Dispatchers.IO) {
            localStorageDataSource.delete(name)
        }
    }
}
