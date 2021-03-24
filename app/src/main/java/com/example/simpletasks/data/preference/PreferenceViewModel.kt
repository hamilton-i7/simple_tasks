package com.example.simpletasks.data.preference

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.simpletasks.data.user.User
import com.example.simpletasks.data.user.UserViewModel

class PreferenceViewModel(
    private val user: User,
    private val userViewModel: UserViewModel
) : ViewModel() {

    var isExpanded by  mutableStateOf(user.preferences.completedTasksExpanded)
        private set

    fun onExpandChange() {
        isExpanded = !isExpanded
        val updatedUser = User(
            id = user.id,
            preferences = Preference(completedTasksExpanded = isExpanded)
        )
        userViewModel.updateUser(updatedUser)
    }
}

@Suppress("UNCHECKED_CAST")
class PreferenceViewModelFactory(
    private val user: User,
    private val userViewModel: UserViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        PreferenceViewModel(user, userViewModel) as T
}