package com.example.nomoosugar.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.repository.ChallengeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// --- The ViewModel ---
// This handles the random timer and picking the random message
class NoMooSugarNavigationViewModel(private val challengeRepository: ChallengeRepository) : ViewModel() {

    // Simple state holder
    data class CowState(
        val isSpeaking: Boolean = false,
        val message: String = "",
        val completedChallenge: ChallengeEntity? = null
    )

    private val _cowState = MutableStateFlow(CowState())
    val cowState = _cowState.asStateFlow()

    fun showChallengeCompletedDialog(challenge: ChallengeEntity) {
        _cowState.update { it.copy(completedChallenge = challenge) }
    }

    fun dismissChallengeCompletedDialog() {
        _cowState.update { it.copy(completedChallenge = null) }
    }

    private val messages = listOf(
        "Hello!",
        "Yummy!",
        "Stay Strong!",
        "Moo!",
        "Healthy!",
        "What did you eat today?",
        "Keep it low sweet!",
        "How are you doing?"
    )

    init {
        startCowLoop()
        observeChallengeCompletions()
    }

    private fun observeChallengeCompletions() {
        viewModelScope.launch {
            challengeRepository.challengeCompleted.collect { challenge ->
                challenge?.let {
                    delay(1000)
                    showChallengeCompletedDialog(it)
                }
            }
        }
    }

    private fun startCowLoop() {
        viewModelScope.launch {
            while (true) {
                // 1. Wait for a random time (e.g., between 5 and 15 seconds)
                val waitTime = Random.nextLong(5000, 15000)
                delay(waitTime)

                // 2. Start Speaking
                _cowState.update {
                    it.copy(
                        isSpeaking = true,
                        message = messages.random()
                    )
                }

                // 3. Speak for 3 seconds
                delay(3000)

                // 4. Stop Speaking
                _cowState.update { it.copy(isSpeaking = false) }
            }
        }
    }
}
