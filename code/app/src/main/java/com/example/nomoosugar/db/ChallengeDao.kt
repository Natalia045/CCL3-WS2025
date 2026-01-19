package com.example.nomoosugar.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.nomoosugar.db.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    @Query("SELECT * FROM challenges") // Removed WHERE userId = :userId
    fun getAllChallenges(): Flow<List<ChallengeEntity>> // Removed userId parameter

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    // Activates a challenge by setting isActive to true
    @Query("UPDATE challenges SET isActive = 1 WHERE id = :id")
    suspend fun activateChallenge(id: Int)

    // for the initial setup of the challenges from assets JSON file
    @Query("SELECT COUNT(*) FROM challenges")
    suspend fun getCount(): Int

    @Query("SELECT * FROM challenges")
    suspend fun getAllOnce(): List<ChallengeEntity> // Removed userId parameter
}