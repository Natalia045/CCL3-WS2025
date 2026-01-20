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

    fun activateChallenge(id: Int) {
        viewModelScope.launch {
            val challengeToActivate = challengeRepository.getAllOnce().firstOrNull { it.id == id }

            challengeToActivate?.let {
                // When activating, set isActive, but keep progress at 0.
                // Set lastUpdated to 0 as a flag that the first entry is still needed.
                val updatedChallenge = it.copy(
                    isActive = true,
                    currentCount = 0,
                    isCompleted = false,
                    lastUpdated = 0L // Use 0 to indicate the challenge has started but no progress is logged
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