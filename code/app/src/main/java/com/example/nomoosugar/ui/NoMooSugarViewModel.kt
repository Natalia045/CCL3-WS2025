package com.example.nomoosugar.ui

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class SugarItem(val label: String, val grams: Float)

class SugarViewModel : ViewModel() {
    var dailyGoal = mutableFloatStateOf(50f)
    var today = mutableStateListOf<SugarItem>()

    fun addSugar(label: String, grams: Float) {
        today.add(SugarItem(label, grams))
    }

    fun total() = today.sumOf { it.grams.toDouble() }.toFloat()
}
