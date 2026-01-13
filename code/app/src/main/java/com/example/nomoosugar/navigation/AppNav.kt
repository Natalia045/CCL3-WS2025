/*
package com.example.nomoosugar.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.nomoosugar.ui.add.AddScreen
import com.example.nomoosugar.ui.home.HomeScreen
import com.example.nomoosugar.ui.profile.SettingsScreen
import com.example.nomoosugar.ui.SugarViewModel

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val vm: SugarViewModel = viewModel()

    NavHost(nav, "home") {
        composable("home") { HomeScreen(nav, vm) }
        composable("add") { AddScreen(nav, vm) }
        composable("settings") { SettingsScreen(nav, vm) }
    }
}
*/