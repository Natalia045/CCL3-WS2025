package com.example.nomoosugar.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class ChallengeEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val startDate: Long,
    val endDate: Long? = null,
    val targetSugarLimit: Double? = null,
)