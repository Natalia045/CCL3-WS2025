package com.example.nomoosugar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.SugarEntryEntity
import com.example.nomoosugar.db.UserProfileEntity
import com.example.nomoosugar.repository.ChallengeRepository
import com.example.nomoosugar.repository.SugarRepository
import com.example.nomoosugar.repository.UserProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
// --- Merged Imports ---
import com.example.nomoosugar.R
import kotlinx.coroutines.launch
import java.time.LocalDate

// Data class to represent the UI state for the HomeScreen
data class HomeUiState(
    val dailySugarLimit: Float = 50.0f,
    val todayTotalSugar: Float = 0f,
    val todayEntries: List<SugarItem> = emptyList(),
    val cowImageResId: Int = R.drawable.cow_white_normal
)

class HomeViewModel(
    private val sugarRepository: SugarRepository,
    private val userProfileRepository: UserProfileRepository,
    private val challengeRepository: ChallengeRepository
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
        val sugarLimit = currentProfile.dailySugarLimit.toFloat()

        // Safety check for null sugar (from challengesnew)
        val safeTotalSugar = (totalSugar ?: 0.0).toFloat()

        // Image Logic (from HEAD)
        val imageResId = when {
            safeTotalSugar == 0f && sugarLimit == 0f -> R.drawable.cow_white_happy
            safeTotalSugar == 0f -> R.drawable.cow_white_normal
            safeTotalSugar < sugarLimit / 2 -> R.drawable.cow_white_happy
            safeTotalSugar > sugarLimit -> R.drawable.cow_white_ate_lot_of_sugar
            else -> R.drawable.cow_white_normal
        }

        HomeUiState(
            dailySugarLimit = sugarLimit,
            todayTotalSugar = safeTotalSugar,
            todayEntries = entriesList,
            cowImageResId = imageResId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun addFoodEntry(label: String, sugarAmount: Double) {
        viewModelScope.launch {
            val entry = SugarEntryEntity(
                label = label,
                amount = sugarAmount,
                timestamp = LocalDate.now().toEpochDay()
            )
            sugarRepository.insert(entry)
            challengeRepository.updateSugarStreakChallenge(LocalDate.now())
            challengeRepository.updateSnackSmarterChallenge(sugarAmount)
        }
    }
}

data class SugarItem(val id: Long, val label: String, val grams: Float)