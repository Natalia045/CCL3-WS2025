package com.example.nomoosugar.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: Long = 1L, // Changed to fixed ID and not auto-generated
    val name: String = "John Doe",
    val dailySugarLimit: Double = 50.0,
)