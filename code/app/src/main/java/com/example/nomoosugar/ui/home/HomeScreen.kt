package com.example.nomoosugar.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.nomoosugar.ui.SugarViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavController, vm: SugarViewModel) {
    val total = vm.total()


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("add") }) {
                Text("+")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("NoMooSugar") },
                actions = {
                    TextButton(onClick = { nav.navigate("settings") }) {
                        Text("⚙")
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("$total / ${vm.dailyGoal.value} g", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            vm.today.forEach {
                Text("${it.label}: ${it.grams} g")
            }
        }
    }
}