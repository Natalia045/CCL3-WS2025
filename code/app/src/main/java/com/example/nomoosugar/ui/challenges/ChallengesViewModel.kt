package com.example.nomoosugar.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.db.UserProfileEntity
import com.example.nomoosugar.repository.ChallengeRepository
import com.example.nomoosugar.repository.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate


// This data class holds only the challenges needed for the UI.
data class ChallengesUiState(
    val allChallenges: List<ChallengeEntity> = emptyList(), // The full list of challenges
    val userPoints: Int = 0
)

class ChallengesViewModel(
    private val challengeRepository: ChallengeRepository, // Injecting the ChallengeRepository
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    // Exposes the UI state with the list of challenges to the ChallengesScreen.
    val uiState: StateFlow<ChallengesUiState> = combine(
        challengeRepository.getAllChallenges(),
        userProfileRepository.getUserProfile()
    ) { challenges, userProfile ->
        val currentUserProfile = userProfile ?: UserProfileEntity()
        ChallengesUiState(
            allChallenges = challenges,
            userPoints = currentUserProfile.points
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChallengesUiState()
    )

    // Refined function to activate a challenge
    fun activateChallenge(id: Int) {
        viewModelScope.launch {
            val challengeToActivate = challengeRepository.getAllOnce().firstOrNull { it.id == id }

            challengeToActivate?.let {
                val updatedChallenge = it.copy(
                    isActive = true,
                    currentCount = 0, // Reset progress when activating
                    isCompleted = false, // Ensure it's not marked as completed
                    lastUpdated = LocalDate.now().toEpochDay() // Set to today
                )
                challengeRepository.updateChallenge(updatedChallenge)
            }
        }
    }

    private fun completeChallenge(challengeType: Int, progress: Int = 1) {
        viewModelScope.launch {
            val challenge = challengeRepository.getAllOnce().firstOrNull {
                it.challengeType == challengeType && !it.isCompleted
            }
            challenge?.let {
                val updatedChallenge = it.copy(isCompleted = true, currentCount = progress)
                challengeRepository.updateChallenge(updatedChallenge)
            }
        }
    }
}