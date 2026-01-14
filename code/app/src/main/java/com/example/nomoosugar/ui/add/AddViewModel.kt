package com.example.nomoosugar.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nomoosugar.db.SugarEntryEntity
import com.example.nomoosugar.repository.SugarRepository
import kotlinx.coroutines.launch

class AddViewModel(
    private val sugarRepository: SugarRepository
) : ViewModel() {

    fun addSugarEntry(label: String, amount: Double, foodId: Long? = null) {
        viewModelScope.launch {
            val entry = SugarEntryEntity(
                id = 0L,
                amount = amount,
                timestamp = System.currentTimeMillis(),
                foodId = foodId,
                label = label
            )
            sugarRepository.addEntry(entry)
        }
    }
}
