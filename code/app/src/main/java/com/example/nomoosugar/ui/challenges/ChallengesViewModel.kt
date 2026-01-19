package com.example.nomoosugar.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.repository.ChallengeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    // Dummy function for the "Start" button - does nothing for now, just prints a message.
    fun activateChallenge(id: Int) { // Renamed from startChallenge to activateChallenge as per your original VM's naming
        viewModelScope.launch {
            println("Activate button clicked for challenge ID: $id - (functionality not implemented yet)")
            // You would later implement actual activation logic here:
            // challengeRepository.activateChallenge(id)
        }
    }
}