package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.datasource.LocalStorageDataSourceContract
import dev.seabat.ramennote.domain.model.RunStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class LocalImageRepository(
    private val localStorageDataSource: LocalStorageDataSourceContract
) : LocalImageRepositoryContract {
    override suspend fun save(imageBytes: ByteArray, name: String) {
        withContext(Dispatchers.IO) {
            localStorageDataSource.save(imageBytes, name)
        }
    }

    override suspend fun load(name: String): RunStatus<ByteArray> =
        withContext(Dispatchers.IO) {
            when (val image = localStorageDataSource.load(name)) {
                null -> RunStatus.Error("画像がありません")
                else -> RunStatus.Success(image)
            }
        }

    override suspend fun getFilePath(name: String): String? =
        withContext(Dispatchers.IO) {
            localStorageDataSource.getFilePath(name)
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
