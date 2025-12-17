package dev.seabat.ramennote.data.repository

interface SettingsRepositoryContract {
    suspend fun getSetting(key: String): String?

    suspend fun setSetting(key: String, value: String)

    suspend fun deleteSetting(key: String)

    suspend fun getAllSettings(): Map<String, String>
}

