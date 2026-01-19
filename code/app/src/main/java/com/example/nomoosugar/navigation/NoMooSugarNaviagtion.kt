package com.example.nomoosugar.navigation

import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

enum class Routes(val route: String) {
    Home("home"),
    Challenges("challenges"),
    Profile("profile"),
    Add("add"),
    Edit("edit/{entryId}")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoMooSugarNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = getTitleForRoute(navController),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Text("🐄", style = MaterialTheme.typography.headlineMedium)
                },
                actions = {
                    when {
                        currentRoute == Routes.Add.route -> {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
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
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
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
            composable(Routes.Home.route) {
                HomeScreen(nav = navController)
            }
            composable(Routes.Challenges.route) {
                ChallengesScreen(navController = navController)
            }
            composable(Routes.Profile.route) {
                ProfileScreen()
            }
            composable(Routes.Add.route) {
                AddScreen(nav = navController)
            }
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
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
