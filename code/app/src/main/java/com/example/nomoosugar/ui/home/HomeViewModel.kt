package com.example.nomoosugar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.UserProfileEntity
import com.example.nomoosugar.repository.SugarRepository
import com.example.nomoosugar.repository.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Data class to represent the UI state for the HomeScreen
data class HomeUiState(
    val dailySugarLimit: Float = 50.0f, // Daily sugar limit from UserProfile
    val todayTotalSugar: Float = 0f,    // Total sugar consumed today
    val todayEntries: List<SugarItem> = emptyList() // List of sugar entries for today
)

class HomeViewModel(
    private val sugarRepository: SugarRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val todayEntries = sugarRepository.getTodayEntries()

    val todayTotal = sugarRepository.getTodayTotalSugar()

    val todayEntriesList = todayEntries.map { entries ->
        entries.map { entry ->
            SugarItem(
                id = entry.id,
                label = entry.label ?: entry.foodId?.let { "Food $it" } ?: "Custom Entry",
                grams = entry.amount.toFloat()
            )
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        userProfileRepository.getUserProfile(),
        todayTotal,
        todayEntriesList
    ) { userProfile, totalSugar, entriesList ->
        val currentProfile = userProfile ?: UserProfileEntity()
        HomeUiState(
            dailySugarLimit = currentProfile.dailySugarLimit.toFloat(),
            todayTotalSugar = (totalSugar ?: 0.0).toFloat(),
            todayEntries = entriesList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}

data class SugarItem(val id: Long, val label: String, val grams: Float)