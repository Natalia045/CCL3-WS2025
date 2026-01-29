package com.example.nomoosugar.repository

import com.example.nomoosugar.db.UserProfileDao
import com.example.nomoosugar.db.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    suspend fun insertUserProfile(userProfile: UserProfileEntity) {
        userProfileDao.insert(userProfile)
    }
    fun getUserProfile(): Flow<UserProfileEntity?> {
        return userProfileDao.getUserProfile()
    }

    suspend fun addPoints(points: Int) {
        val userProfile = userProfileDao.getUserProfile().first()
        if (userProfile != null) {
            val updatedProfile = userProfile.copy(points = userProfile.points + points)
            userProfileDao.insert(updatedProfile)
        }
    }
}