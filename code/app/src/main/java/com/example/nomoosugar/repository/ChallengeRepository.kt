package com.example.nomoosugar.repository

import com.example.nomoosugar.db.ChallengeDao
import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.db.UserProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

class ChallengeRepository(
    private val dao: ChallengeDao,
    private val userProfileRepository: UserProfileRepository
) {
    private val _challengeCompleted = MutableStateFlow<ChallengeEntity?>(null)
    val challengeCompleted = _challengeCompleted.asStateFlow()
    fun getAllChallenges() = dao.getAllChallenges()

    suspend fun insertChallenges(challenges: List<ChallengeEntity>) = dao.insertChallenges(challenges)

    suspend fun updateChallenge(challenge: ChallengeEntity) {
        dao.updateChallenge(challenge)
        if (challenge.isCompleted) {
            _challengeCompleted.value = challenge
        }
    }

    suspend fun getAllOnce(): List<ChallengeEntity> = dao.getAllOnce()

    suspend fun activateChallenge(id: Int) {
        val challenge = dao.getAllOnce().firstOrNull { it.id == id } ?: return
        updateChallenge(
            challenge.copy(
                isActive = true,
                currentCount = 0,
                isCompleted = false,
                lastUpdated = 0L
            )
        )
    }
    private suspend fun awardPoints(points: Int) {
        val profile = userProfileRepository.getUserProfile().firstOrNull() ?: UserProfileEntity()
        val updated = profile.copy(points = profile.points + points)
        userProfileRepository.insertUserProfile(updated)
    }
    suspend fun updateReadTheLabelChallenge() {
        val readLabel = dao.getChallengeByType(1) ?: return
        if (!readLabel.isActive) return

        val newCount = (readLabel.currentCount + 1).coerceAtMost(readLabel.targetCount)
        val completed = newCount >= readLabel.targetCount

        if (completed && !readLabel.isCompleted) {
            awardPoints(20)
        }
        updateChallenge(readLabel.copy(currentCount = newCount, isCompleted = completed))
    }

    suspend fun updateSnackSmarterChallenge(sugarAmount: Double) {
        val challenge = dao.getActiveChallenge(2) ?: return
        if (sugarAmount > 10) return

        val newCount = (challenge.currentCount + 1).coerceAtMost(challenge.targetCount)
        val completed = newCount >= challenge.targetCount

        if (completed && !challenge.isCompleted) {
            awardPoints(30)
        }
        updateChallenge(challenge.copy(currentCount = newCount, isCompleted = completed))
    }
    suspend fun updateGoalSeekerChallenge() {
        val challenge = dao.getActiveChallenge(4) ?: return
        if (challenge.isCompleted) return

        awardPoints(10)
        updateChallenge(challenge.copy(currentCount = 1, isCompleted = true))
    }

    suspend fun updateSugarStreakChallenge(logDate: LocalDate) {
        val challenge = dao.getActiveChallenge(3) ?: return
        if (challenge.isCompleted) return
        if (challenge.lastUpdated == 0L) {
            updateChallenge(challenge.copy(currentCount = 1, lastUpdated = logDate.toEpochDay()))
            return
        }

        val lastUpdate = LocalDate.ofEpochDay(challenge.lastUpdated)
        val today = logDate

        when {
            lastUpdate.plusDays(1) == today -> {
                val newCount = (challenge.currentCount + 1).coerceAtMost(challenge.targetCount)
                val completed = newCount >= challenge.targetCount

                if (completed && !challenge.isCompleted) {
                    awardPoints(30)
                }

                updateChallenge(challenge.copy(
                    currentCount = newCount,
                    isCompleted = completed,
                    lastUpdated = today.toEpochDay()
                ))
            }
            lastUpdate == today -> {}
            else -> updateChallenge(challenge.copy(currentCount = 1, lastUpdated = today.toEpochDay()))
        }
    }
}