package com.example.simpletasks.data.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.simpletasks.data.SimpleTasksDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val repo: SettingsRepo

    init {
        val settingsDao =
            SimpleTasksDatabase.getDatabase(application, applicationScope).settingsDao()
        repo = SettingsRepo(settingsDao)
    }

    fun readSettings(): LiveData<Settings> = repo.readSettings().asLiveData()

    fun onExpandChange(settings: Settings) {
        val updatedSettings = Settings(
            id = settings.id,
            completedTasksExpanded = !settings.completedTasksExpanded
        )
        updateSettings(updatedSettings)
    }

    private suspend fun readSettingsFlow(): StateFlow<Settings> =
        repo.readSettings().stateIn(applicationScope)

    private fun updateSettings(settings: Settings) = viewModelScope.launch {
        repo.updateSettings(settings)
    }

    private fun deleteAllSettings() = viewModelScope.launch {
        repo.deleteAllSettings()
    }
}