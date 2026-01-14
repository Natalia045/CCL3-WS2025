package com.example.nomoosugar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nomoosugar.NoMooSugarApplication
import com.example.nomoosugar.ui.add.AddViewModel
import com.example.nomoosugar.ui.home.HomeViewModel

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as NoMooSugarApplication

            return when {
                modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(application.sugarRepository) as T
                }
                modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                    AddViewModel(application.sugarRepository) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
