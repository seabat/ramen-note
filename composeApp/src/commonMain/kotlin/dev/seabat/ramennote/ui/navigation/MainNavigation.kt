package dev.seabat.ramennote.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
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
import dev.seabat.ramennote.ui.screens.FutureScreen
import dev.seabat.ramennote.ui.screens.HomeScreen
import dev.seabat.ramennote.ui.screens.NoteScreen
import dev.seabat.ramennote.ui.screens.ScheduleScreen
import dev.seabat.ramennote.ui.screens.SettingsScreen
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.event_note_24px
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.screen_future_title
import ramennote.composeapp.generated.resources.screen_home_title
import ramennote.composeapp.generated.resources.screen_note_title
import ramennote.composeapp.generated.resources.screen_schedule_title
import ramennote.composeapp.generated.resources.screen_settings_title
import ramennote.composeapp.generated.resources.signpost_24px

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Note : Screen("note")
    object Future : Screen("future")
    object Settings : Screen("settings")
    
    @Composable
    fun getIcon(): ImageVector {
        return when (this) {
            Home -> Icons.Default.Home
            Schedule -> vectorResource(Res.drawable.event_note_24px)
            Note -> vectorResource(Res.drawable.book_5_24px)
            Future -> vectorResource(Res.drawable.signpost_24px)
            Settings -> Icons.Default.Settings
        }
    }

    @Composable
    fun getTitle(): String {
        return when (this) {
            Home -> stringResource(Res.string.screen_home_title)
            Schedule -> stringResource(Res.string.screen_schedule_title)
            Note -> stringResource(Res.string.screen_note_title)
            Future -> stringResource(Res.string.screen_future_title)
            Settings ->stringResource(Res.string.screen_settings_title)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    val screens = listOf(
        Screen.Home,
        Screen.Schedule,
        Screen.Note,
        Screen.Future,
        Screen.Settings
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.getIcon(), contentDescription = screen.getTitle()) },
                        label = { Text(screen.getTitle()) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen()
            }
            composable(Screen.Note.route) {
                NoteScreen()
            }
            composable(Screen.Future.route) {
                FutureScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
