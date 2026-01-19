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

    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    // Activates a challenge by setting isActive to true

    // for the initial setup of the challenges from assets JSON file
    @Query("SELECT COUNT(*) FROM challenges")
    suspend fun getCount(): Int

    @Query("SELECT * FROM challenges")
    suspend fun getAllOnce(): List<ChallengeEntity>

    @Query("UPDATE challenges SET isActive = 1 WHERE id = :id")
    suspend fun activateChallenge(id: Int)
}