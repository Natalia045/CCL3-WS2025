package com.example.nomoosugar.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.FoodEntity
import com.example.nomoosugar.db.SugarEntryEntity
import com.example.nomoosugar.repository.ChallengeRepository
import com.example.nomoosugar.repository.FoodRepository
import com.example.nomoosugar.repository.SugarRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddViewModel(
    private val sugarRepository: SugarRepository,
    private val foodRepository: FoodRepository,
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<FoodEntity>>(emptyList())
    val searchResults: StateFlow<List<FoodEntity>> = _searchResults.asStateFlow()
    private var searchJob: Job? = null

    fun searchFoods(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) _searchResults.value = emptyList()
            else foodRepository.searchFoods(query).collect { foods -> _searchResults.value = foods }
        }
    }

    fun addSugarEntry(label: String, amount: Double, isManual: Boolean) {
        viewModelScope.launch {
            val entry = SugarEntryEntity(
                label = label,
                amount = amount,
                timestamp = LocalDate.now().toEpochDay()
            )
            sugarRepository.insert(entry)

            challengeRepository.updateSugarStreakChallenge(LocalDate.now())
            challengeRepository.updateSnackSmarterChallenge(amount)
            if (isManual) challengeRepository.updateReadTheLabelChallenge()
        }
    }
}
