package com.example.simpletasks.data.settings

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings")
    fun readSettings(): Flow<Settings>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSettings(settings: Settings)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateSettings(settings: Settings)

    @Query("DELETE FROM settings")
    suspend fun deleteAllSettings()
}