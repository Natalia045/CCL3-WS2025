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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addChallenge(challengeEntity: ChallengeEntity)

    @Update
    suspend fun updateChallenge(challengeEntity: ChallengeEntity)

    @Delete
    suspend fun deleteChallenge(challengeEntity: ChallengeEntity)

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun findChallengeById(id: Int): ChallengeEntity

    @Query("SELECT * FROM challenges")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>
}