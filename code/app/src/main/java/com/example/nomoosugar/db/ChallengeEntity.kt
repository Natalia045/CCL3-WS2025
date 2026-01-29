package com.example.nomoosugar.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val challengeType: Int,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val isActive: Boolean = false,
    val isCompleted: Boolean = false,
    val lastUpdated: Long = 0L
)