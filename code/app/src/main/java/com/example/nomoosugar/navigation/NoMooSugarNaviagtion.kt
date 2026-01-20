package com.example.nomoosugar.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.nomoosugar.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween

// --- 1. The ViewModel ---
// This handles the random timer and picking the random message
class NavigationViewModel : ViewModel() {

    // Simple state holder
    data class CowState(
        val isSpeaking: Boolean = false,
        val message: String = ""
    )

    private val _cowState = MutableStateFlow(CowState())
    val cowState = _cowState.asStateFlow()

    private val messages = listOf(
        "Hello!",
        "Yummi!",
        "No Sugar!",
        "Stay Strong!",
        "Moo!",
        "Healthy!"
    )

    init {
        startCowLoop()
    }

    private fun startCowLoop() {
        viewModelScope.launch {
            while (true) {
                // 1. Wait for a random time (e.g., between 5 and 15 seconds)
                val waitTime = Random.nextLong(5000, 15000)
                delay(waitTime)

                // 2. Start Speaking
                _cowState.update {
                    it.copy(
                        isSpeaking = true,
                        message = messages.random()
                    )
                }

                // 3. Speak for 3 seconds
                delay(3000)

                // 4. Stop Speaking
                _cowState.update { it.copy(isSpeaking = false) }
            }
        }
    }
}

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
    viewModel: NavigationViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Observe the Cow State
    val cowState by viewModel.cowState.collectAsState()

    Scaffold(
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
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                },
                actions = {
                    when {
                        currentRoute == Routes.Add.route -> {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Filled.Close, "Close", tint = Color.White)
                            }
                        }
                        currentRoute?.startsWith("edit/") == true -> {
                            IconButton(
                                onClick = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("requestDiscard", true)
                                }
                            ) {
                                Icon(Icons.Filled.Close, "Close", tint = Color.White)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (currentRoute != Routes.Add.route) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
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
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        // I increased the padding slightly so the big text doesn't feel cramped
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = text,
                color = Color.Black,
                // Option A: Use a larger Theme style
                style = MaterialTheme.typography.headlineSmall,

                // Option B: (Uncomment below) Set a specific size if Option A isn't big enough
                // fontSize = 24.sp,

                fontWeight = FontWeight.ExtraBold // Made it ExtraBold to pop more
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
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
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