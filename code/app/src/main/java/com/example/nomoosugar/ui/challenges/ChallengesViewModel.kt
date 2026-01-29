package com.example.nomoosugar.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.db.UserProfileEntity
import com.example.nomoosugar.repository.ChallengeRepository
import com.example.nomoosugar.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChallengesUiState(
    val allChallenges: List<ChallengeEntity> = emptyList(),
    val userPoints: Int = 0
)

class ChallengesViewModel(
    private val challengeRepository: ChallengeRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _challengeCompleted = MutableStateFlow<ChallengeEntity?>(null)
    val challengeCompleted: StateFlow<ChallengeEntity?> = _challengeCompleted.asStateFlow()

    fun dismissChallengeCompletedDialog() {
        _challengeCompleted.value = null
    }
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

    init {
        viewModelScope.launch {
            challengeRepository.challengeCompleted.collectLatest {
                _challengeCompleted.value = it
            }
        }
    }

    fun activateChallenge(id: Int) {
        viewModelScope.launch {
            val challengeToActivate = challengeRepository.getAllOnce().firstOrNull { it.id == id }

            challengeToActivate?.let {
                val updatedChallenge = it.copy(
                    isActive = true,
                    currentCount = 0,
                    isCompleted = false,
                    lastUpdated = 0L
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
                _challengeCompleted.value = updatedChallenge
            }
        }
    }
}