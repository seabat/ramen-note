package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.datasource.AppVersionDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AppVersionRepository : AppVersionRepositoryContract {
    private val appVersionDataSource = AppVersionDataSource()

    override suspend fun getVersionName(): String =
        withContext(Dispatchers.IO) {
            appVersionDataSource.getVersionName()
        }

    override suspend fun getVersionCode(): Int =
        withContext(Dispatchers.IO) {
            appVersionDataSource.getVersionCode()
        }
}

