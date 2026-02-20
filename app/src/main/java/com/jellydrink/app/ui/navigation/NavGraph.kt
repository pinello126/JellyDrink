package com.jellydrink.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jellydrink.app.ui.screens.BadgesScreen
import com.jellydrink.app.ui.screens.HistoryScreen
import com.jellydrink.app.ui.screens.HomeScreen
import com.jellydrink.app.ui.screens.ProfileSettingsScreen
import com.jellydrink.app.ui.screens.ShopScreen

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Profile : Screen("profile", "Profilo", Icons.Filled.Person, Icons.Outlined.Person)
    data object History : Screen("history", "Storico", Icons.Filled.History, Icons.Outlined.History)
    data object Badges : Screen("badges", "Badge", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents)
}

// Additional routes (not in bottom nav)
object Routes {
    const val SHOP = "shop"
}

val screens = listOf(Screen.Home, Screen.Profile, Screen.History, Screen.Badges)

@Composable
fun JellyDrinkNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Check if current route should show bottom bar
    val showBottomBar = currentDestination?.route in screens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    screens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon
                                    else screen.unselectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToShop = {
                        navController.navigate(Routes.SHOP)
                    }
                )
            }
            composable(Screen.Profile.route) { ProfileSettingsScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Badges.route) { BadgesScreen() }
            composable(Routes.SHOP) {
                ShopScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
