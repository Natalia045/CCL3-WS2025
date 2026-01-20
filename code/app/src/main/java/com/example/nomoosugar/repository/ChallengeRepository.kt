package com.example.nomoosugar.repository

import com.example.nomoosugar.db.ChallengeDao
import com.example.nomoosugar.db.ChallengeEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ChallengeRepository(
    private val dao: ChallengeDao,
    private val userProfileRepository: UserProfileRepository
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

    suspend fun updateSnackSmarterChallenge(sugarAmount: Double) {
        val snackSmarterChallenge = dao.getActiveChallenge(2)
        snackSmarterChallenge?.let {
            if (!it.isCompleted && sugarAmount <= 10) {
                val newCount = it.currentCount + 1
                if (newCount >= it.targetCount) {
                    dao.updateChallenge(it.copy(currentCount = newCount, isCompleted = true))
                    userProfileRepository.addPoints(30)
                } else {
                    dao.updateChallenge(it.copy(currentCount = newCount))
                }
            }
        }
    }
    suspend fun updateGoalSeekerChallenge() {
        val goalSeeker = dao.getActiveChallenge(4)

        goalSeeker?.let {
            if (!it.isCompleted) {
                dao.updateChallenge(
                    it.copy(
                        currentCount = 1,
                        isCompleted = true
                    )
                )
                userProfileRepository.addPoints(30)
            }
        }
    }

    suspend fun updateSugarStreakChallenge(logDate: LocalDate) {
        val streakChallenge = dao.getActiveChallenge(1) // 1 for streak challenge
        streakChallenge?.let {
            // First day of the challenge
            if (it.lastUpdated == 0L) {
                dao.updateChallenge(it.copy(currentCount = 1, lastUpdated = logDate.toEpochDay()))
            } else {
                val lastUpdate = LocalDate.ofEpochDay(it.lastUpdated)
                val today = logDate

                when {
                    // Continue streak
                    lastUpdate.plusDays(1) == today -> {
                        val newCount = it.currentCount + 1
                        if (newCount >= it.targetCount) {
                            // Challenge complete
                            dao.updateChallenge(it.copy(currentCount = newCount, isCompleted = true))
                            userProfileRepository.addPoints(100) // award points
                        } else {
                            // Update progress
                            dao.updateChallenge(it.copy(currentCount = newCount, lastUpdated = today.toEpochDay()))
                        }
                    }
                    // Same day, do nothing
                    lastUpdate == today -> {}
                    // Streak broken
                    else -> {
                        dao.updateChallenge(it.copy(currentCount = 1, lastUpdated = today.toEpochDay()))
                    }
                }
            }
        }
    }
}