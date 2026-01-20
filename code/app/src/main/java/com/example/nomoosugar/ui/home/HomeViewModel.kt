package com.example.nomoosugar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.UserProfileEntity // Import UserProfileEntity
import com.example.nomoosugar.repository.SugarRepository
import com.example.nomoosugar.repository.UserProfileRepository // Import UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine // Import combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import com.example.nomoosugar.R // Import R for drawable resources

// Data class to represent the UI state for the HomeScreen
data class HomeUiState(
    val dailySugarLimit: Float = 50.0f, // Daily sugar limit from UserProfile
    val todayTotalSugar: Float = 0f,    // Total sugar consumed today
    val todayEntries: List<SugarItem> = emptyList(), // List of sugar entries for today
    val cowImageResId: Int = R.drawable.cow_white_normal // Resource ID for the cow image
)

class HomeViewModel(
    private val sugarRepository: SugarRepository,
    private val userProfileRepository: UserProfileRepository // Now injecting UserProfileRepository
) : ViewModel() {

    // REMOVE THE FOLLOWING:
    // private val _dailyGoal = MutableStateFlow(50f)
    // val dailyGoal: StateFlow<Float> = _dailyGoal.asStateFlow()

    val allEntries = sugarRepository.observeAllEntries()

    val todayEntries = allEntries.map { entries ->
        val today = getTodayStartTimestamp()
        entries.filter { it.timestamp >= today }
    }

    val todayTotal = todayEntries.map { entries ->
        entries.sumOf { it.amount.toDouble() }.toFloat()
    }

    val todayEntriesList = todayEntries.map { entries ->
        entries.map { entry ->
            SugarItem(
                id = entry.id,
                label = entry.label ?: entry.foodId?.let { "Food $it" } ?: "Custom Entry",
                grams = entry.amount.toFloat()
            )
        }
    }

    // New StateFlow for HomeUiState, combining data from both repositories
    val uiState: StateFlow<HomeUiState> = combine(
        userProfileRepository.getUserProfile(), // Flow of UserProfileEntity?
        todayTotal,                            // Flow of Float (today's total sugar)
        todayEntriesList                       // Flow of List<SugarItem>
    ) { userProfile, totalSugar, entriesList ->
        val currentProfile = userProfile ?: UserProfileEntity()
        val sugarLimit = currentProfile.dailySugarLimit.toFloat()
        val imageResId = when {
            totalSugar == 0f && sugarLimit == 0f -> R.drawable.cow_white_happy
            totalSugar == 0f -> R.drawable.cow_white_normal
            totalSugar < sugarLimit / 2 -> R.drawable.cow_white_happy
            totalSugar > sugarLimit -> R.drawable.cow_white_ate_lot_of_sugar
            else -> R.drawable.cow_white_normal
        }
        HomeUiState(
            dailySugarLimit = sugarLimit,
            todayTotalSugar = totalSugar,
            todayEntries = entriesList,
            cowImageResId = imageResId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState() // Initial state for HomeUiState
    )

    // REMOVE THE FOLLOWING:
    // fun setDailyGoal(goal: Float) {
    //     _dailyGoal.value = goal
    // }

    private fun getTodayStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}

data class SugarItem(val id: Long, val label: String, val grams: Float)