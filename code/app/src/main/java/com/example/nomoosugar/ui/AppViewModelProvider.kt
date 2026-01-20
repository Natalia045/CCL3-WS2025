package com.example.nomoosugar.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nomoosugar.NoMooSugarApplication
import com.example.nomoosugar.ui.add.AddViewModel
import com.example.nomoosugar.ui.home.HomeViewModel
import com.example.nomoosugar.ui.challenges.ChallengesViewModel
import com.example.nomoosugar.ui.profile.ProfileViewModel
import com.example.nomoosugar.repository.ChallengeRepository // Import ChallengeRepository

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application = this[APPLICATION_KEY] as NoMooSugarApplication
            HomeViewModel(application.sugarRepository, application.userProfileRepository, application.challengeRepository)
        }
        initializer {
            val application = this[APPLICATION_KEY] as NoMooSugarApplication
            AddViewModel(application.sugarRepository, application.foodRepository, application.challengeRepository)
        }
        initializer {
            val application = this[APPLICATION_KEY] as NoMooSugarApplication
            // Provide ChallengeRepository to ChallengesViewModel
            ChallengesViewModel(application.challengeRepository, application.userProfileRepository)
        }
        initializer {
            val application = this[APPLICATION_KEY] as NoMooSugarApplication
            ProfileViewModel(application, application.userProfileRepository, application.challengeRepository)
        }
        initializer {
            val application = this[APPLICATION_KEY] as NoMooSugarApplication
            com.example.nomoosugar.navigation.NavigationViewModel()
        }
    }
}