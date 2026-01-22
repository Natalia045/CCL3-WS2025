package com.example.nomoosugar.ui.challenges

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.nomoosugar.R
import com.example.nomoosugar.db.ChallengeEntity
import com.example.nomoosugar.ui.AppViewModelProvider
import kotlinx.coroutines.launch

@Composable
fun ChallengesScreen(
    nav: NavController,
    viewModel: ChallengesViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val allChallenges = uiState.allChallenges
    val completedChallenge by viewModel.challengeCompleted.collectAsState()

    // Filter challenges based on isActive and isCompleted
    val active = allChallenges.filter { it.isActive && !it.isCompleted }
    val available = allChallenges.filter { !it.isActive && !it.isCompleted }
    val completed = allChallenges.filter { it.isCompleted }

    if (completedChallenge != null) {
        ChallengeCompletedDialog(
            challenge = completedChallenge!!,
            onDismiss = { viewModel.dismissChallengeCompletedDialog() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOTAL MOO-POINTS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "${uiState.userPoints}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Active Challenges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        if (active.isEmpty()) {
            Text("No active challenges yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            active.forEach { challenge ->
                ActiveChallengeCard(challenge)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Try New Challenges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        if (available.isEmpty()) {
            Text("No new challenges available.", style = MaterialTheme.typography.bodyMedium)
        } else {
            available.forEach { challenge ->
                AvailableChallengeCard(challenge) {
                    viewModel.activateChallenge(challenge.id) // Call ViewModel's activate function
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Completed Challenges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        if (completed.isEmpty()) {
            Text("No completed challenges yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            completed.forEach { challenge ->
                CompletedChallengeCard(
                    icon = Icons.Default.EmojiEvents,
                    title = challenge.title,
                    description = challenge.description
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ActiveChallengeCard(challenge: ChallengeEntity) {
    val progress = if (challenge.targetCount > 0) challenge.currentCount.toFloat() / challenge.targetCount else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(challenge.title, fontWeight = FontWeight.Bold)
                    Text(challenge.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text("${challenge.currentCount}/${challenge.targetCount}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun AvailableChallengeCard(
    challenge: ChallengeEntity,
    onStartClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(challenge.title, fontWeight = FontWeight.Bold)
                Text(challenge.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(onClick = onStartClick) {
                Text("Start")
            }
        }
    }
}

@Composable
fun CompletedChallengeCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD49DE9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ChallengeCompletedDialog(challenge: ChallengeEntity, onDismiss: () -> Unit) {
    val rotation = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color(0xFFD49DE9),
        targetValue = Color(0xFFAEC6F5),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "color"
    )

    LaunchedEffect(Unit) {
        launch {
            rotation.animateTo(
                targetValue = 15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 300, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            rotation.animateTo(
                targetValue = -15f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 300, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Challenge Completed!") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.cow_white_happy),
                    contentDescription = "Happy Cow",
                    modifier = Modifier
                        .size(128.dp)
                        .rotate(rotation.value)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("You've completed the '${challenge.title}' challenge!")
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF53027B)
                )
            ) {
                Text("Hooray!")
            }
        },
        containerColor = animatedColor
    )
}