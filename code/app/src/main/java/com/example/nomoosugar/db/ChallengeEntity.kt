package com.example.nomoosugar.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Unique identifier for each challenge
    val challengeType: Int, // Identifies the type of challenge (e.g., 1 for streak, 2 for daily, etc.)
    val title: String,      // Display name of the challenge (e.g., "Sugar-Free Streak")
    val description: String,// A brief explanation of the challenge
    val targetCount: Int,   // The goal number for the challenge (e.g., 7 days, 3 weekends)
    val currentCount: Int = 0, // How much progress the user has made towards the target
    // val rewardPoints: Int, // REMOVED: Not needed for now
    val isActive: Boolean = false, // Is the user currently trying to complete this challenge?
    val isCompleted: Boolean = false, // Has the user successfully completed this challenge?
    val lastUpdated: Long = 0L // Timestamp (e.g., epoch day) when progress was last recorded, useful for streaks
)