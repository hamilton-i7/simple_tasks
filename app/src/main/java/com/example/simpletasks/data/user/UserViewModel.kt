package com.example.simpletasks.data.user

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.simpletasks.data.SimpleTasksDatabase
import com.example.simpletasks.data.preference.Preference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val applicationScope = CoroutineScope(SupervisorJob())
    private val repo: UserRepo

    init {
        val userDao = SimpleTasksDatabase.getDatabase(application, applicationScope).userDao()
        repo = UserRepo(userDao)
    }

    @Composable
    fun readUser(): State<User> =
        repo.readUser().collectAsState(initial = User(preferences = Preference()))

    fun updateUser(user: User) = viewModelScope.launch {
        repo.updateUser(user)
    }
}