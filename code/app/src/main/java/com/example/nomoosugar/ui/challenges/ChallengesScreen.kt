package com.example.nomoosugar.ui.challenges

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChallengesScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            /* Icon Placeholder */
            Spacer(Modifier.width(8.dp))
            Text("Challenges")
        }

        Spacer(Modifier.height(8.dp))
        Text("You are doing great!")

        Spacer(Modifier.height(16.dp))
        // Total Points
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TOTAL MOO-POINTS")
                    Text("0") // Placeholder
                }
                /* Trophy Icon Placeholder */
            }
        }

        Spacer(Modifier.height(16.dp))
        // Active Challenges
        Text("Active Challenges")
        Spacer(Modifier.height(8.dp))
        repeat(2) { // Placeholder items
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        /* Challenge Icon Placeholder */
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Challenge Title")
                            Text("Challenge Description")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(progress = 0.5f)
                    Text("50%")
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        // New Challenges
        Text("Try New Challenges")
        Spacer(Modifier.height(8.dp))
        repeat(2) {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        /* Challenge Icon Placeholder */
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("New Challenge")
                            Text("Description")
                        }
                    }
                    Button(onClick = {}) {
                        Text("Start")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        // Completed Challenges
        Text("Completed")
        Spacer(Modifier.height(8.dp))
        repeat(2) {
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                    /* Challenge Icon Placeholder */
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Completed Challenge")
                        Text("Description")
                    }
                }
            }
        }
    }
}
