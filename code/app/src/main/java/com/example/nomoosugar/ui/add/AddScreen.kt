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

    Column(Modifier.padding(16.dp)) {
        Text("Quick Add")

        Row {
            Button(onClick = { vm.addSugar("Coffee", 5f); nav.popBackStack() }) { Text("Coffee 5g") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.addSugar("Fruit", 10f); nav.popBackStack() }) { Text("Fruit 10g") }
        }

        Row {
            Button(onClick = { vm.addSugar("Snack", 15f); nav.popBackStack() }) { Text("Snack 15g") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { vm.addSugar("Soda", 35f); nav.popBackStack() }) { Text("Soda 35g") }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(grams, { grams = it }, label = { Text("Sugar (g)") })
        OutlinedTextField(label, { label = it }, label = { Text("Label") })

        Button(onClick = {
            vm.addSugar(label, grams.toFloat())
            nav.popBackStack()
        }) {
            Text("Add")
        }
    }
}
