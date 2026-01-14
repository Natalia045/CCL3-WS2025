package com.example.nomoosugar.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.repository.SugarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class HomeViewModel(
    private val sugarRepository: SugarRepository
) : ViewModel() {

    private val _dailyGoal = MutableStateFlow(50f)
    val dailyGoal: StateFlow<Float> = _dailyGoal.asStateFlow()

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

    fun setDailyGoal(goal: Float) {
        _dailyGoal.value = goal
    }

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
