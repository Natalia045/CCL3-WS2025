package com.example.nomoosugar.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.FoodEntity
import com.example.nomoosugar.db.SugarEntryEntity
import com.example.nomoosugar.repository.FoodRepository
import com.example.nomoosugar.repository.SugarRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddViewModel(
    private val sugarRepository: SugarRepository,
    private val foodRepository: FoodRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<FoodEntity>>(emptyList())
    val searchResults: StateFlow<List<FoodEntity>> = _searchResults.asStateFlow()

    private var searchJob: Job? = null

    fun searchFoods(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
            } else {
                foodRepository.searchFoods(query).collect { foods ->
                    _searchResults.value = foods
                }
            }
        }
    }

    fun addSugarEntry(label: String, amount: Double) {
        viewModelScope.launch {
            val entry = SugarEntryEntity(
                id = 0L,
                amount = amount,
                timestamp = System.currentTimeMillis(),
                foodId = null,
                label = label
            )
            sugarRepository.addEntry(entry)
        }
    }
}
