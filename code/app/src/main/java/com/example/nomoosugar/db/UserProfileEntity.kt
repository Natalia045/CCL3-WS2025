package com.example.nomoosugar.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val avatarUri: String? = null,
    val dailySugarLimit: Double,
    val notificationsEnabled: Boolean = true,
)