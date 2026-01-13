package com.example.nomoosugar.ui.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nomoosugar.ui.SugarViewModel

@Composable
fun AddScreen(nav: NavController, vm: SugarViewModel) {
    var label by remember { mutableStateOf("") }
    var grams by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") } // For search bar

    Column(Modifier.padding(16.dp)) {
        Text("Quick Add", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.addSugar("Coffee", 5f); nav.popBackStack() }) { Text("Coffee 5g") }
            Button(onClick = { vm.addSugar("Fruit", 10f); nav.popBackStack() }) { Text("Fruit 10g") }
        }

        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { vm.addSugar("Snack", 15f); nav.popBackStack() }) { Text("Snack 15g") }
            Button(onClick = { vm.addSugar("Soda", 35f); nav.popBackStack() }) { Text("Soda 35g") }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search (placeholder)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = grams,
            onValueChange = { grams = it },
            label = { Text("Sugar (g)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text("Label") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (grams.isNotEmpty()) {
                    vm.addSugar(label, grams.toFloat())
                    nav.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add")
        }
    }
}
