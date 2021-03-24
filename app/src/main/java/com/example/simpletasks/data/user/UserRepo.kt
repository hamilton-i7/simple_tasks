package com.example.simpletasks.data.user

import kotlinx.coroutines.flow.Flow

class UserRepo(private val userDao: UserDao) {

    fun readUser(): Flow<User> = userDao.readUser()

    suspend fun addUser(user: User) = userDao.addUser(user)

    suspend fun updateUser(user: User) = userDao.updateUser(user)

    suspend fun deleteAllUsers() = userDao.deleteAllUsers()
}