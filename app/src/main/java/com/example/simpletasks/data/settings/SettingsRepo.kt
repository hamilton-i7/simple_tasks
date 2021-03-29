package com.example.simpletasks.data.settings

import kotlinx.coroutines.flow.Flow

class SettingsRepo(private val settingsDao: SettingsDao) {

    fun readSettings(): Flow<Settings> = settingsDao.readSettings()

    suspend fun updateSettings(settings: Settings) =
        settingsDao.updateSettings(settings)

}