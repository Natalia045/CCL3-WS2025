package com.example.nomoosugar.navigation

import com.example.nomoosugar.ui.AppViewModelProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nomoosugar.ui.add.AddScreen
import com.example.nomoosugar.ui.challenges.ChallengesScreen
import com.example.nomoosugar.ui.edit.EditScreen
import com.example.nomoosugar.ui.home.HomeScreen
import com.example.nomoosugar.ui.profile.ProfileScreen
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import com.example.nomoosugar.R
import com.example.nomoosugar.db.ChallengeEntity
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.nomoosugar.ui.theme.CloseButtonLightBlueOpacity
import com.example.nomoosugar.ui.theme.HomeTitleBlue
import com.example.nomoosugar.ui.theme.CardBackgroundBlue
import com.example.nomoosugar.ui.theme.CloseButtonLightModeTint
import com.example.nomoosugar.ui.theme.FabBlue
import com.example.nomoosugar.ui.theme.NavBarGray
import com.example.nomoosugar.ui.theme.AppBlack
import androidx.compose.ui.graphics.Color // Import Color for Color.White
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.rememberInfiniteTransition

enum class Routes(val route: String) {
    Home("home"),
    Challenges("challenges"),
    Profile("profile"),
    Add("add"),
    Edit("edit/{entryId}")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoMooSugarNavigation(
    // Inject the ViewModel here
    viewModel: NoMooSugarNavigationViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Observe the Cow State
    val cowState by viewModel.cowState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Logic: If speaking, show bubble. If not, show Route Title.
                    if (cowState.isSpeaking) {
                        SpeechBubble(text = cowState.message)
                    } else {
                        Text(
                            text = getTitleForRoute(navController),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    // We animate based on the boolean state (isSpeaking)
                    AnimatedContent(
                        targetState = cowState.isSpeaking,
                        transitionSpec = {
                            // This creates a "Pop" effect: Scale In + Fade In
                            (scaleIn() + fadeIn(animationSpec = tween(200))).togetherWith(
                                scaleOut() + fadeOut(animationSpec = tween(200))
                            )
                        },
                        label = "CowAnimation"
                    ) { isSpeaking ->
                        // This lambda provides the "targetState" boolean to decide what to render
                        val iconRes = if (isSpeaking) {
                            R.drawable.cow_speaking
                        } else {
                            R.drawable.cow_eating_sugar
                        }

                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "Cow Icon",
                            // Optional: You can make the speaking cow slightly larger if needed
                            modifier = Modifier
                                .padding(4.dp)
                                .size(48.dp)
                        )
                    }
                },
                actions = {
                    when {
                        currentRoute == Routes.Add.route -> {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .padding(end = 8.dp) // Added padding
                                    .background(
                                        color = if (isSystemInDarkTheme()) Color(0xFF606060) else CloseButtonLightModeTint, // Dark gray background in dark mode
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(Icons.Filled.Close, "Close", tint = if (isSystemInDarkTheme()) Color.White else FabBlue) // White tint in dark mode
                            }
                        }
                        currentRoute?.startsWith("edit/") == true -> {
                            IconButton(
                                onClick = { navController.currentBackStackEntry?.savedStateHandle?.set("requestDiscard", true) },
                                modifier = Modifier
                                    .padding(end = 8.dp) // Added padding
                                    .background(
                                        color = if (isSystemInDarkTheme()) Color(0xFF606060) else CloseButtonLightModeTint, // Dark gray background in dark mode
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(Icons.Filled.Close, "Close", tint = if (isSystemInDarkTheme()) Color.White else FabBlue) // White tint in dark mode
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else CardBackgroundBlue,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            val routesWithoutBottomBar = listOf(Routes.Add.route, Routes.Edit.route)
            if (currentRoute !in routesWithoutBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        if (cowState.completedChallenge != null) {
            ChallengeCompletedDialog(
                challenge = cowState.completedChallenge!!,
                onDismiss = { viewModel.dismissChallengeCompletedDialog() }
            )
        }
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.Home.route) { HomeScreen(nav = navController) }
            composable(Routes.Challenges.route) { ChallengesScreen(nav = navController) }
            composable(Routes.Profile.route) { ProfileScreen() }
            composable(Routes.Add.route) { AddScreen(nav = navController) }
            composable(
                route = Routes.Edit.route,
                arguments = listOf(navArgument("entryId") { type = androidx.navigation.NavType.LongType })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
                EditScreen(nav = navController, entryId = entryId)
            }
        }
    }
}


@Composable
fun SpeechBubble(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            // 1. Try to resize based on length
            val fontSize = when {
                text.length > 25 -> 13.sp  // Very long text
                text.length > 15 -> 16.sp  // Medium text
                else -> 22.sp              // Short & punchy
            }

            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = fontSize,
                fontWeight = FontWeight.ExtraBold,
                
                // 2. The "Safety Net" for the worst case
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis // "What did you eat tod..."
            )
        }
    }
}

@Composable
fun getTitleForRoute(navController: NavHostController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route ?: return "NoMooSugar"
    return when (route) {
        Routes.Home.route -> "Home"
        Routes.Challenges.route -> "Challenges"
        Routes.Profile.route -> "Profile"
        Routes.Add.route -> "Add Sugar"
        else -> if (route.startsWith("edit/")) "Edit Entry" else "NoMooSugar"
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

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val items = listOf(
            Triple(Routes.Home, "Home", Icons.Filled.Home),
            Triple(Routes.Challenges, "Challenges", Icons.Filled.EmojiEvents),
            Triple(Routes.Profile, "Profile", Icons.Filled.AccountCircle)
        )

        items.forEach { (route, label, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
                selected = currentDestination?.hierarchy?.any { it.route == route.route } == true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = {
                    navController.navigate(route.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
