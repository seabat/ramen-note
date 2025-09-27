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
import androidx.navigation.toRoute
import dev.seabat.ramennote.ui.screens.note.addarea.AddAreaScreen
import dev.seabat.ramennote.ui.screens.note.shoplist.AreaShopListScreen
import dev.seabat.ramennote.ui.screens.note.shop.ShopScreen
import dev.seabat.ramennote.ui.screens.note.editarea.EditAreaScreen
import dev.seabat.ramennote.ui.screens.withbottom.FutureScreen
import dev.seabat.ramennote.ui.screens.withbottom.HomeScreen
import dev.seabat.ramennote.ui.screens.note.NoteScreen
import dev.seabat.ramennote.ui.screens.withbottom.ScheduleScreen
import dev.seabat.ramennote.ui.screens.withbottom.SettingsScreen
import dev.seabat.ramennote.domain.model.Shop as ShopInfo
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.event_note_24px
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.screen_area_shop_title
import ramennote.composeapp.generated.resources.screen_future_title
import ramennote.composeapp.generated.resources.screen_home_title
import ramennote.composeapp.generated.resources.screen_schedule_title
import ramennote.composeapp.generated.resources.screen_settings_title
import ramennote.composeapp.generated.resources.signpost_24px

sealed interface Screen {
    companion object {
        fun getRouteName(clazz: KClass<out Screen>): String {
            return clazz.simpleName ?: "Unknown"
        }
    }

    @Serializable
    data object Home : Screen {
        override val route: String = getRouteName(Home::class)
        @Composable
        override fun getIcon(): ImageVector {
            return Icons.Default.Home
        }
        @Composable
        override fun getTitle(): String {
            return stringResource(Res.string.screen_home_title)
        }
    }

    @Serializable
    data object Schedule : Screen {
        override val route: String = getRouteName(Schedule::class)
        @Composable
        override fun getIcon(): ImageVector {
            return vectorResource(Res.drawable.event_note_24px)
        }
        @Composable
        override fun getTitle(): String {
            return stringResource(Res.string.screen_schedule_title)
        }
    }

    @Serializable
    data object Note : Screen {
        override val route: String = getRouteName(Note::class)
        @Composable
        override fun getIcon(): ImageVector {
            return vectorResource(Res.drawable.book_5_24px)
        }
        @Composable
        override fun getTitle(): String {
            return "ノート"
        }
    }

    @Serializable
    data object Future : Screen {
        override val route: String = getRouteName(Future::class)
        @Composable
        override fun getIcon(): ImageVector {
            return vectorResource(Res.drawable.signpost_24px)
        }
        @Composable
        override fun getTitle(): String {
            return stringResource(Res.string.screen_future_title)
        }
    }

    @Serializable
    data object Settings : Screen {
        override val route: String = getRouteName(Settings::class)
        @Composable
        override fun getIcon(): ImageVector {
            return Icons.Default.Settings
        }
        @Composable
        override fun getTitle(): String {
            return stringResource(Res.string.screen_settings_title)
        }
    }

    @Serializable
    data class AreaShopList(val areaName: String): Screen {
        override val route: String = getRouteName(AreaShopList::class)
        @Composable
        override fun getIcon(): ImageVector {
            return Icons.Default.Star // 表示されないので適切なアイコン
        }
        @Composable
        override fun getTitle(): String {
            return stringResource(Res.string.screen_area_shop_title)
        }
    }

    @Serializable
    data class EditArea(val areaName: String): Screen {
        override val route: String = getRouteName(EditArea::class)
        @Composable
        override fun getIcon(): ImageVector { return Icons.Default.Edit }
        @Composable
        override fun getTitle(): String { return "編集" }
    }

    @Serializable
    data object AddArea : Screen {
        override val route: String = getRouteName(AddArea::class)
        @Composable
        override fun getIcon(): ImageVector { return Icons.Default.Add }
        @Composable
        override fun getTitle(): String { return "登録" }
    }

    @Serializable
    data class Shop(val shopJson: String) : Screen {
        override val route: String = getRouteName(Shop::class)
        @Composable
        override fun getIcon(): ImageVector { return Icons.Default.Star }
        @Composable
        override fun getTitle(): String { 
            return try {
                val shop = ShopInfo.fromJsonString(shopJson)
                shop.name
            } catch (e: Exception) {
                "店舗詳細"
            }
        }
    }

    val route: String
    @Composable
    fun getIcon(): ImageVector
    @Composable
    fun getTitle(): String
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    val tabScreens = listOf(
        Screen.Home,
        Screen.Schedule,
        Screen.Note,
        Screen.Future,
        Screen.Settings
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val withBottomNavigation = currentDestination?.hierarchy?.any { destination ->
        destination.route?.let { route ->
            tabScreens.any { screen -> route.contains(screen.route) }
        } == true // null と比較する場合もあるので == true を使用する
    } == true // null と比較する場合もあるので == true を使用する

    Scaffold(
        bottomBar = {
            if (withBottomNavigation) {
                NavigationBar {
                    tabScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.getIcon(), contentDescription = screen.getTitle()) },
                            label = { Text(screen.getTitle()) },
                            selected = currentDestination.hierarchy.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen) {
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Screen.Home> {
                HomeScreen()
            }
            composable<Screen.Schedule> {
                ScheduleScreen()
            }
            composable<Screen.Note> {
                NoteScreen(
                    onAreaClick = { areaName ->
                        navController.navigate(Screen.AreaShopList(areaName))
                    },
                    onAddAreaClick = {
                        navController.navigate(Screen.AddArea)
                    },
                    onAreaLongClick = { areaName ->
                        navController.navigate(Screen.EditArea(areaName))
                    }
                )
            }
            composable<Screen.Future> {
                FutureScreen()
            }
            composable<Screen.Settings> {
                SettingsScreen()
            }
            composable<Screen.AreaShopList> { backStackEntry ->
                val screen: Screen.AreaShopList = backStackEntry.toRoute()
                AreaShopListScreen(
                    areaName = screen.areaName,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onShopClick = { shop ->
                        navController.navigate(Screen.Shop(shop.toJsonString()))
                    }
                )
            }
            composable<Screen.AddArea> {
                AddAreaScreen(
                    onBackClick = { navController.popBackStack() },
                    onCompleted = { navController.popBackStack() }
                )
            }
            composable<Screen.EditArea> { backStackEntry ->
                val screen: Screen.EditArea = backStackEntry.toRoute()
                EditAreaScreen(
                    areaName = screen.areaName,
                    onBackClick = { navController.popBackStack() },
                    onCompleted = { navController.popBackStack() }
                )
            }
            composable<Screen.Shop> { backStackEntry ->
                val screen: Screen.Shop = backStackEntry.toRoute()
                val shop = try {
                    ShopInfo.fromJsonString(screen.shopJson)
                } catch (e: Exception) {
                    // エラーの場合はデフォルトのShopオブジェクトを作成
                    ShopInfo(
                        name = "エラー",
                        shopUrl = "",
                        mapUrl = "",
                        star = 0,
                        stationName = "",
                        category = ""
                    )
                }
                ShopScreen(
                    shop = shop,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
