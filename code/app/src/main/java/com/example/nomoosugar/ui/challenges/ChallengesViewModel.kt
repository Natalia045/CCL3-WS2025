package com.example.nomoosugar.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.repository.ChallengeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate


// This data class holds only the challenges needed for the UI.
data class ChallengesUiState(
    val allChallenges: List<ChallengeEntity> = emptyList() // The full list of challenges
)

class ChallengesViewModel(
    private val challengeRepository: ChallengeRepository // Injecting the ChallengeRepository
) : ViewModel() {

    // Exposes the UI state with the list of challenges to the ChallengesScreen.
    val uiState: StateFlow<ChallengesUiState> =
        challengeRepository.getAllChallenges().map { challenges -> // Calls repository, no userId needed
            ChallengesUiState(allChallenges = challenges)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // Keeps flow active while UI is visible
            initialValue = ChallengesUiState() // Initial state with an empty list
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
}