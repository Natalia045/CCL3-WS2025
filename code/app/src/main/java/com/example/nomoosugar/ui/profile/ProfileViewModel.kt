package com.example.nomoosugar.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.nomoosugar.db.UserProfileEntity
import com.example.nomoosugar.notifications.ReminderWorker
import com.example.nomoosugar.repository.ChallengeRepository
import com.example.nomoosugar.repository.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class ProfileUiState(
    val name: String = "Default User",
    val dailySugarLimit: Float = 50.0f,
    val points: Int = 0,
    val notificationsEnabled: Boolean = false
)

class ProfileViewModel(
    application: Application,
    private val userProfileRepository: UserProfileRepository,
    private val challengeRepository: ChallengeRepository
) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    val uiState: StateFlow<ProfileUiState> =
        userProfileRepository.getUserProfile().map { userProfile ->
            val currentProfile = userProfile ?: UserProfileEntity()
            ProfileUiState(
                name = currentProfile.name,
                dailySugarLimit = currentProfile.dailySugarLimit.toFloat(),
                points = currentProfile.points,
                notificationsEnabled = currentProfile.notificationsEnabled
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState()
        )

    fun updateDailySugarLimit(newLimit: Float) {
        viewModelScope.launch {
            val existingUserProfile = userProfileRepository.getUserProfile().first() ?: UserProfileEntity()
            val updatedUserProfile = existingUserProfile.copy(dailySugarLimit = newLimit.toDouble())
            userProfileRepository.insertUserProfile(updatedUserProfile)
            challengeRepository.updateGoalSeekerChallenge()
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val existingUserProfile = userProfileRepository.getUserProfile().first() ?: UserProfileEntity()
            val updatedUserProfile = existingUserProfile.copy(notificationsEnabled = enabled)
            userProfileRepository.insertUserProfile(updatedUserProfile)

            val workName = "sugar_reminder"

            if (enabled) {
                val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(2, TimeUnit.HOURS)
                    .build()
                workManager.enqueueUniquePeriodicWork(
                    workName,
                    ExistingPeriodicWorkPolicy.REPLACE,
                    reminderRequest
                )
            } else {
                workManager.cancelUniqueWork(workName)
            }
        }
    }
}
