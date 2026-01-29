package com.example.nomoosugar.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomoosugar.ui.AppViewModelProvider
import com.example.nomoosugar.ui.theme.AppBlack
import com.example.nomoosugar.ui.theme.ProgressTrackBlend
import com.example.nomoosugar.ui.theme.HomeTitleBlue
import com.example.nomoosugar.ui.theme.Orange75
import androidx.compose.foundation.isSystemInDarkTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(nav: NavController) {
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsState()
    val dailyGoal = uiState.dailySugarLimit
    val todayTotal = uiState.todayTotalSugar
    val todayEntries = uiState.todayEntries

    val progress = if (dailyGoal > 0) (todayTotal / dailyGoal).coerceAtMost(1f) else 0f
    val isOverLimit = todayTotal > dailyGoal
    val progressColor = when {
        isOverLimit -> Color(0xFFD32F2F)
        todayTotal / dailyGoal >= 0.75f -> Orange75
        else -> HomeTitleBlue
    }

    val baseStrokeWidth = 20.dp
    val maxOverLimitStrokeWidth = 28.dp

    val currentStrokeWidth = when {
        todayTotal / dailyGoal <= 1f -> baseStrokeWidth
        todayTotal / dailyGoal > 2f -> maxOverLimitStrokeWidth
        else -> {
            val overLimitRatio = (todayTotal / dailyGoal - 1f).coerceIn(0f, 1f)
            baseStrokeWidth + (maxOverLimitStrokeWidth - baseStrokeWidth) * overLimitRatio
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { nav.navigate("add") },
                containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else HomeTitleBlue
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimary else Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(0.9f),
                    strokeWidth = currentStrokeWidth,
                    strokeCap = StrokeCap.Round,
                    color = progressColor,
                    trackColor = ProgressTrackBlend
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = uiState.cowImageResId),
                        contentDescription = "Cow Status",
                        modifier = Modifier.size(240.dp)
                    )

                }
            }

            Text(
                text = "${todayTotal.toInt()}g / ${dailyGoal.toInt()}g",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (isOverLimit) Color(0xFFD32F2F) else AppBlack
            )


            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Today\'s Intake",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (todayEntries.isEmpty()) {
                Text(
                    text = "No entries yet. Tap + to add your first entry!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    todayEntries.forEach { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { nav.navigate("edit/${item.id}") },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else AppBlack
                                )
                                Text(
                                    text = "${item.grams.toInt()} g",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurface else AppBlack
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
