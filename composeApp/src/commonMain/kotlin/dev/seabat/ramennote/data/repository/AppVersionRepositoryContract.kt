package dev.seabat.ramennote.data.repository

interface AppVersionRepositoryContract {
    suspend fun getVersionName(): String

    suspend fun getVersionCode(): Int
}
