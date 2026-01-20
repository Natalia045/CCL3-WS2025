package com.example.nomoosugar.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "Default User",
    val dailySugarLimit: Double = 50.0,
    val points: Int = 0,
    val notificationsEnabled: Boolean = false
)