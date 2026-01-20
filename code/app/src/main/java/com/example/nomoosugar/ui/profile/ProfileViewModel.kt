package com.example.nomoosugar.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.UserProfileEntity
import com.example.nomoosugar.repository.ChallengeRepository
import com.example.nomoosugar.repository.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// This data class directly holds what your ProfileScreen needs to show.
data class ProfileUiState(
    val name: String = "Default User",       // String for displaying text in the UI
    val dailySugarLimit: Float = 50.0f,       // Float for the Slider composable in the UI
    val points: Int = 0
)

class ProfileViewModel(
    private val userProfileRepository: UserProfileRepository, // Dependency injected
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    // 1. Function/Property to get name and sugar limit from DB and expose to UI:
    // This StateFlow continuously provides the latest UI state by mapping from the repository.
    val uiState: StateFlow<ProfileUiState> =
        userProfileRepository.getUserProfile().map { userProfile ->
            // If userProfile from DB is null, use defaults from UserProfileEntity
            // (or from ProfileUiState defaults if entity also has defaults)
            val currentProfile = userProfile ?: UserProfileEntity()
            ProfileUiState(
                name = currentProfile.name,
                dailySugarLimit = currentProfile.dailySugarLimit.toFloat(), // Convert Double to Float
                points = currentProfile.points
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000), // Keep active while UI is visible
            initialValue = ProfileUiState() // Initial state with default values
        )

    // 2. Function to update the daily sugar limit:
    fun updateDailySugarLimit(newLimit: Float) { // Accepts Float from the UI slider
        viewModelScope.launch {
            // Get the current user profile entity from the repository (to preserve its ID and name)
            var existingUserProfile = userProfileRepository.getUserProfile().first() ?: UserProfileEntity()
            // Create an updated entity with the new sugar limit (convert Float back to Double)
            var updatedUserProfile = existingUserProfile.copy(dailySugarLimit = newLimit.toDouble())

            // after that let´s complete the challenge
            val goalSeekerChallenge = challengeRepository.getAllOnce().firstOrNull {
                it.challengeType == 4 && !it.isCompleted
            }
            goalSeekerChallenge?.let {
                val updatedChallenge = it.copy(isCompleted = true, currentCount = 1)
                challengeRepository.updateChallenge(updatedChallenge)
                addPoints(30)
            }
            // Save the updated entity back to the database
            userProfileRepository.insertUserProfile(updatedUserProfile)
        }
    }

    fun addPoints(pointsToAdd: Int) {
        viewModelScope.launch {
            val userProfile = userProfileRepository.getUserProfile().first() ?: return@launch
            val updatedProfile = userProfile.copy(points = userProfile.points + pointsToAdd)
            userProfileRepository.insertUserProfile(updatedProfile)
        }
    }
}