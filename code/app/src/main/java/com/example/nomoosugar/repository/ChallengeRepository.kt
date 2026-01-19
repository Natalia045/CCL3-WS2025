package com.example.nomoosugar.repository

import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.db.ChallengeDao
import kotlinx.coroutines.flow.Flow

class ChallengeRepository(private val challengeDao: ChallengeDao) {

    val challenges = challengeDao.getAllChallenges()

    suspend fun addChallenge(challengeEntity: ChallengeEntity) {
        // TODO: Implement addChallenge
    }

    suspend fun updateChallenge(challengeEntity: ChallengeEntity) {
        // TODO: Implement updateChallenge
    }

    suspend fun deleteChallenge(challengeEntity: ChallengeEntity) {
        // TODO: Implement deleteChallenge
    }

    suspend fun findChallengeById(challengeId: Int): ChallengeEntity {
        // TODO: Implement findChallengeById
        return challengeDao.findChallengeById(challengeId)
    }
}
