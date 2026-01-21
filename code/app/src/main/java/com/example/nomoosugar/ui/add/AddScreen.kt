package com.example.nomoosugar.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomoosugar.ui.AppViewModelProvider
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.sp
import com.example.nomoosugar.ui.theme.AppBlack
import com.example.nomoosugar.ui.theme.CardBackgroundBlue
import com.example.nomoosugar.ui.theme.HomeTitleBlue

@Composable
fun AddScreen(nav: NavController) {
    val viewModel: AddViewModel = viewModel(factory = AppViewModelProvider.Factory)
    var label by remember { mutableStateOf("Food") }
    var grams by remember { mutableStateOf("0") }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()

    LaunchedEffect(searchQuery) { delay(300); viewModel.searchFoods(searchQuery) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        /*
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                "What did you eat today?",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }



        Spacer(modifier = Modifier.height(24.dp))
        QuickAddSection(viewModel, nav)

         */
        Spacer(modifier = Modifier.height(24.dp))
        SearchFoodSection(searchQuery, onQueryChange = { searchQuery = it }, searchResults, onSelect = {
            label = it.name
            grams = it.sugarAmount.toString()
            searchQuery = ""
        })
        Spacer(modifier = Modifier.height(24.dp))
        CustomEntrySection(label, grams, onLabelChange = { label = it }, onGramsChange = { grams = it }) {
            val gramsValue = grams.toDoubleOrNull() ?: 0.0
            if (gramsValue > 0) {
                viewModel.addSugarEntry(label.ifEmpty { "Food" }, gramsValue, isManual = true)
                nav.popBackStack()
            }
        }
    }
}

/*
@Composable
fun QuickAddSection(viewModel: AddViewModel, nav: NavController) {
    Text("Quick Add", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickAddButton("Coffee 5g") { viewModel.addSugarEntry("Coffee", 5.0, false); nav.popBackStack() }
        QuickAddButton("Fruit 10g") { viewModel.addSugarEntry("Fruit", 10.0, false); nav.popBackStack() }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        QuickAddButton("Snack 15g") { viewModel.addSugarEntry("Snack", 15.0, false); nav.popBackStack() }
        QuickAddButton("Soda 35g") { viewModel.addSugarEntry("Soda", 35.0, false); nav.popBackStack() }
    }
}
*/

@Composable
fun SearchFoodSection(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    searchResults: List<com.example.nomoosugar.db.FoodEntity>,
    onSelect: (com.example.nomoosugar.db.FoodEntity) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            label = { Text("Search Food or Drink") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 56.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(searchResults) { food ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(food) }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(food.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("${food.sugarAmount}g sugar", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun CustomEntrySection(
    label: String,
    grams: String,
    onLabelChange: (String) -> Unit,
    onGramsChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Text("Custom Entry", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(value = grams, onValueChange = onGramsChange, label = { Text("Sugar (g)") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(12.dp))
    OutlinedTextField(value = label, onValueChange = onLabelChange, label = { Text("Label") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(24.dp))
    Button(onClick = onAddClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp)) {
        Text("Add", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

/*
@Composable
fun QuickAddButton(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.height(56.dp), shape = RoundedCornerShape(12.dp)) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}
*/


@Composable
fun QuickAddSection(viewModel: AddViewModel, nav: NavController) {
    Text(
        "Quick Add", 
        style = MaterialTheme.typography.titleLarge, 
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(16.dp))
    
    // Row 1
    Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAddButton(
            label = "Coffee",
            amount = "5g",
            modifier = Modifier.weight(1f),
            onClick = { viewModel.addSugarEntry("Coffee", 5.0, false); nav.popBackStack() }
        )
        QuickAddButton(
            label = "Fruit",
            amount = "10g",
            modifier = Modifier.weight(1f),
            onClick = { viewModel.addSugarEntry("Fruit", 10.0, false); nav.popBackStack() }
        )
    }
    
    Spacer(modifier = Modifier.height(12.dp))
    
    // Row 2
    Row(
        modifier = Modifier.fillMaxWidth(), 
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAddButton(
            label = "Snack",
            amount = "15g",
            modifier = Modifier.weight(1f),
            onClick = { viewModel.addSugarEntry("Snack", 15.0, false); nav.popBackStack() }
        )
        QuickAddButton(
            label = "Soda",
            amount = "35g",
            modifier = Modifier.weight(1f),
            onClick = { viewModel.addSugarEntry("Soda", 35.0, false); nav.popBackStack() }
        )
    }
}

@Composable
fun QuickAddButton(
    label: String, 
    amount: String, 
    modifier: Modifier = Modifier, 
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            // Matches the light blue in the image (from your Color.kt)
            containerColor = CardBackgroundBlue 
        ),
        modifier = modifier.height(100.dp) // Fixed height to match the boxy look
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Top Left Label (Blue)
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = HomeTitleBlue, // Using the specific blue from theme
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.TopStart)
            )

            // Bottom Right Amount (Black, Large)
            Text(
                text = amount,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                color = AppBlack,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}