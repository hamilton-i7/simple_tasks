package com.example.simpletasks.data.user

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user")
    fun readUser(): Flow<User>

    @Insert
    suspend fun addUser(user: User)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateUser(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()
}