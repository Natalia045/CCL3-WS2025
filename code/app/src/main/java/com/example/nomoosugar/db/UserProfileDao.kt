package com.example.nomoosugar.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    // Inserts a UserProfileEntity. If a user with the same PrimaryKey (id=1L) already exists, it will replace it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfileEntity)

    // Retrieves the single user profile with id = 1. Returns null if no user exists.
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    // Checks if a user profile with id = 1 exists. Returns true if it does, false otherwise.
    @Query("SELECT EXISTS(SELECT 1 FROM user_profile WHERE id = 1 LIMIT 1)")
    fun hasUser(): Flow<Boolean>
}