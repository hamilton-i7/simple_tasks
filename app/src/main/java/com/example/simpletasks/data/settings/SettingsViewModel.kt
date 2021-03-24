package com.example.simpletasks.data.settings

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
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

    var isExpanded by  mutableStateOf(false)
        private set

    init {
        val settingsDao =
            SimpleTasksDatabase.getDatabase(application, applicationScope).settingsDao()
        repo = SettingsRepo(settingsDao)

        viewModelScope.launch {
            isExpanded = readSettingsFlow().value.completedTasksExpanded
        }
    }

    @Composable
    fun readSettings(): State<Settings> =
        repo.readSettings().collectAsState(initial = Settings.Default)

    fun onExpandChange(settings: Settings) {
        isExpanded = !isExpanded
        val updatedSettings = Settings(
            id = settings.id,
            completedTasksExpanded = isExpanded
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