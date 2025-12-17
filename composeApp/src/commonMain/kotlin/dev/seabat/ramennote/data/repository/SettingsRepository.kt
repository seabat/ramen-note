package dev.seabat.ramennote.data.repository

import dev.seabat.ramennote.data.database.RamenNoteDatabase
import dev.seabat.ramennote.data.database.dao.SettingDao
import dev.seabat.ramennote.data.database.entity.SettingEntity
import kotlin.lazy

class SettingsRepository(
    private val database: RamenNoteDatabase
) : SettingsRepositoryContract {
    private val settingDao: SettingDao by lazy {
        database.settingDao()
    }

    override suspend fun getSetting(key: String): String? {
        return settingDao.getSettingByKey(key)?.value
    }

    override suspend fun setSetting(key: String, value: String) {
        val entity = SettingEntity(key = key, value = value)
        settingDao.insertSetting(entity)
    }

    override suspend fun deleteSetting(key: String) {
        settingDao.deleteSettingByKey(key)
    }

    override suspend fun getAllSettings(): Map<String, String> {
        val settings = settingDao.getAllSettings()
        return settings.associate { it.key to it.value }
    }
}

