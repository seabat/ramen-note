package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath

class LocalAreaImageRepository(
    private val localStorageDataSource: LocalStorageDataSourceContract
) : LocalAreaImageRepositoryContract {
    
    private val imagePath = "area_image.png".toPath()
    
    override suspend fun save(imageBytes: ByteArray) {
        withContext(Dispatchers.IO) {
            localStorageDataSource.save(imageBytes)
        }
    }
    
    override suspend fun load(): ByteArray? {
        return withContext(Dispatchers.IO) {
            localStorageDataSource.load()
        }
    }
}
