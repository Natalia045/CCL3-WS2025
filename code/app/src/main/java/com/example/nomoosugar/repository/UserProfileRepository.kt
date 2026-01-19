package com.example.nomoosugar.repository

import com.example.nomoosugar.db.UserProfileDao
import com.example.nomoosugar.db.UserProfileEntity
import kotlinx.coroutines.flow.Flow

// UserProfileRepository takes UserProfileDao as a dependency, allowing it to interact with the database.
class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    // Suspended function to insert or update a user profile. It delegates the call to the DAO.
    suspend fun insertUserProfile(userProfile: UserProfileEntity) {
        userProfileDao.insert(userProfile)
    }

    // Returns a Flow of UserProfileEntity?, which emits the current user profile or null if none exists.
    fun getUserProfile(): Flow<UserProfileEntity?> {
        return userProfileDao.getUserProfile()
    }


}