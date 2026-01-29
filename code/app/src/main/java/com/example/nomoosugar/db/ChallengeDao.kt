package com.example.nomoosugar.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {

    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Query("SELECT COUNT(*) FROM challenges")
    suspend fun getCount(): Int

    @Query("SELECT * FROM challenges")
    suspend fun getAllOnce(): List<ChallengeEntity>

    @Query("UPDATE challenges SET isActive = 1 WHERE id = :id")
    suspend fun activateChallenge(id: Int)

    @Query("SELECT * FROM challenges WHERE challengeType = :type AND isActive = 1 AND isCompleted = 0 LIMIT 1")
    suspend fun getActiveChallenge(type: Int): ChallengeEntity?

    @Query("SELECT * FROM challenges WHERE challengeType = :type AND isCompleted = 0 LIMIT 1")
    suspend fun getChallengeByType(type: Int): ChallengeEntity?
}