package com.example.nomoosugar.repository

import com.example.nomoosugar.db.ChallengeDao
import com.example.nomoosugar.db.ChallengeEntity
import kotlinx.coroutines.flow.Flow

class ChallengeRepository(
    private val dao: ChallengeDao
) {
    // Provides a Flow of all challenges for reactive UI updates
    fun getAllChallenges(): Flow<List<ChallengeEntity>> {
        return dao.getAllChallenges()
    }

    // Inserts a list of challenges into the database (used for initial loading from JSON)
    suspend fun insertChallenges(challenges: List<ChallengeEntity>) {
        dao.insertChallenges(challenges)
    }

    // Updates a single challenge in the database
    suspend fun updateChallenge(challenge: ChallengeEntity) {
        dao.updateChallenge(challenge)
    }

    // Activates a specific challenge by its ID (delegates to DAO)
    suspend fun activateChallenge(id: Int) {
        dao.activateChallenge(id)
    }

    // Fetches all challenges once (not as a Flow) for logic processing in ViewModel
    suspend fun getAllOnce(): List<ChallengeEntity> {
        return dao.getAllOnce()
    }

    // This function is for initial population, will be called from NoMooSugarApplication
    suspend fun createInitialChallengesFromJson(challenges: List<ChallengeEntity>) {
        dao.insertChallenges(challenges)
    }
}