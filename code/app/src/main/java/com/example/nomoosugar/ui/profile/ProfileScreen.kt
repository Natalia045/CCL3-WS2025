package com.example.nomoosugar.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nomoosugar.ui.SugarViewModel

@Composable
fun SettingsScreen(nav: NavController, vm: SugarViewModel) {
    var goal by remember { mutableStateOf(vm.dailyGoal.value.toString()) }

    Column(Modifier.padding(16.dp)) {
        Text("Daily Sugar Goal")

        OutlinedTextField(goal, { goal = it })

        Button(onClick = {
            vm.dailyGoal.value = goal.toFloat()
            nav.popBackStack()
        }) {
            Text("Save")
        }
    }
}
