package dev.seabat.ramennote.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.seabat.ramennote.domain.util.createTodayLocalDate
import dev.seabat.ramennote.ui.screens.history.HistoryScreen
import dev.seabat.ramennote.ui.screens.history.editreport.EditReportScreen
import dev.seabat.ramennote.ui.screens.history.report.ReportScreen
import dev.seabat.ramennote.ui.screens.home.HomeScreen
import dev.seabat.ramennote.ui.screens.note.NoteScreen
import dev.seabat.ramennote.ui.screens.note.addarea.AddAreaScreen
import dev.seabat.ramennote.ui.screens.note.addshop.AddShopScreen
import dev.seabat.ramennote.ui.screens.note.editarea.EditAreaScreen
import dev.seabat.ramennote.ui.screens.note.editshop.EditShopScreen
import dev.seabat.ramennote.ui.screens.note.shop.ShopScreen
import dev.seabat.ramennote.ui.screens.note.shoplist.AreaShopListScreen
import dev.seabat.ramennote.ui.screens.schedule.ScheduleScreen
import dev.seabat.ramennote.ui.screens.settings.SettingsScreen
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import ramennote.composeapp.generated.resources.Res
import ramennote.composeapp.generated.resources.book_5_24px
import ramennote.composeapp.generated.resources.event_note_24px
import ramennote.composeapp.generated.resources.ramen_dining_24px
import ramennote.composeapp.generated.resources.screen_area_shop_title
import ramennote.composeapp.generated.resources.screen_history_title
import ramennote.composeapp.generated.resources.screen_home_title
import ramennote.composeapp.generated.resources.screen_schedule_title
import ramennote.composeapp.generated.resources.screen_settings_title
import kotlin.reflect.KClass
import dev.seabat.ramennote.domain.model.Shop as ShopInfo

sealed interface Screen {
    companion object {
        fun getRouteName(clazz: KClass<out Screen>): String = clazz.simpleName ?: "Unknown"
    }

    @Serializable
    data object Home : Screen {
        override val route: String = getRouteName(Home::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Home

        @Composable
        override fun getTitle(): String = stringResource(Res.string.screen_home_title)
    }

    @Serializable
    data object Schedule : Screen {
        override val route: String = getRouteName(Schedule::class)

        @Composable
        override fun getIcon(): ImageVector = vectorResource(Res.drawable.event_note_24px)

        @Composable
        override fun getTitle(): String = stringResource(Res.string.screen_schedule_title)
    }

    @Serializable
    data object Note : Screen {
        override val route: String = getRouteName(Note::class)

        @Composable
        override fun getIcon(): ImageVector = vectorResource(Res.drawable.book_5_24px)

        @Composable
        override fun getTitle(): String = "ノート"
    }

    @Serializable
    data object History : Screen {
        override val route: String = getRouteName(History::class)

        @Composable
        override fun getIcon(): ImageVector = vectorResource(Res.drawable.ramen_dining_24px)

        @Composable
        override fun getTitle(): String = stringResource(Res.string.screen_history_title)
    }

    @Serializable
    data object Settings : Screen {
        override val route: String = getRouteName(Settings::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Settings

        @Composable
        override fun getTitle(): String = stringResource(Res.string.screen_settings_title)
    }

    @Serializable
    data class AreaShopList(
        val areaName: String
    ) : Screen {
        override val route: String = getRouteName(AreaShopList::class)

        @Composable
        override fun getIcon(): ImageVector {
            return Icons.Default.Star // 表示されないので適切なアイコン
        }

        @Composable
        override fun getTitle(): String = stringResource(Res.string.screen_area_shop_title)
    }

    @Serializable
    data class EditArea(
        val areaName: String
    ) : Screen {
        override val route: String = getRouteName(EditArea::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Edit

        @Composable
        override fun getTitle(): String = "編集"
    }

    @Serializable
    data object AddArea : Screen {
        override val route: String = getRouteName(AddArea::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Add

        @Composable
        override fun getTitle(): String = "登録"
    }

    @Serializable
    data class Shop(
        val shopId: Int,
        val shopName: String
    ) : Screen {
        override val route: String = getRouteName(Shop::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Star

        @Composable
        override fun getTitle(): String = shopName
    }

    @Serializable
    data class AddShop(
        val areaName: String
    ) : Screen {
        override val route: String = getRouteName(AddShop::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Add

        @Composable
        override fun getTitle(): String = "店舗登録"
    }

    @Serializable
    data class EditShop(
        val shopJson: String
    ) : Screen {
        override val route: String = getRouteName(EditShop::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Edit

        @Composable
        override fun getTitle(): String =
            try {
                val shop = ShopInfo.fromJsonString(shopJson)
                "${shop.name} 編集"
            } catch (_: Exception) {
                "店舗編集"
            }
    }

    @Serializable
    data class Report(
        val shopId: Int,
        val shopName: String,
        val menuName: String,
        val iso8601Date: String
    ) : Screen {
        override val route: String = getRouteName(Report::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Star

        @Composable
        override fun getTitle(): String = "$shopName レポート"
    }

    @Serializable
    data class EditReport(
        val reportId: Int
    ) : Screen {
        override val route: String = getRouteName(EditReport::class)

        @Composable
        override fun getIcon(): ImageVector = Icons.Default.Star

        @Composable
        override fun getTitle(): String = "レポート編集"
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

    // HistoryScreenに遷移する際のreportIdを管理するState
    var historyReportId by remember { mutableStateOf<Int?>(null) }

    val tabScreens =
        listOf(
            Screen.Home,
            Screen.Schedule,
            Screen.Note,
            Screen.History,
            Screen.Settings
        )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val withBottomNavigation =
        currentDestination?.hierarchy?.any { destination ->
            destination.route?.let { route ->
                tabScreens.any { screen -> route.contains(screen.route) }
            } == true // null と比較する場合もあるので == true を使用する
        } == true // null と比較する場合もあるので == true を使用する

    Scaffold(
        bottomBar = {
            if (withBottomNavigation) {
                NavigationBar {
                    tabScreens.forEach { screen ->
                        val isSelected = currentDestination.route?.endsWith(screen.route) == true
                        // デバッグ用: コンソールに出力
                        println("Screen: ${screen.route}, Current: ${currentDestination.route}, Selected: $isSelected")
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.getIcon(),
                                    contentDescription = screen.getTitle(),
                                    tint =
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                )
                            },
                            label = {
                                Text(
                                    text = screen.getTitle(),
                                    color =
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
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
                HomeScreen(
                    goToShop = { shopId, shopName ->
                        navController.navigate(Screen.Shop(shopId, shopName))
                    },
                    goToReport = { shopId, shopName, menuName, iso8601Date ->
                        navController.navigate(Screen.Report(shopId, shopName, menuName, iso8601Date))
                    },
                    goToHistory = { reportId ->
                        // NavigationBar は restoreState = true を設定しているので、一度 Screen クラスのプロパティを使ってタブ画面に遷移すると 画面遷移する度にそのプロパティが復元されてしまう。
                        // タブ画面への遷移時に使用するパラメータは Screen クラスのプロパティを使用せず、生存期間が長い変数を使用する。
                        historyReportId = reportId
                        navController.navigate(Screen.History) {
                            // タブクリック時と同じ処理で画面遷移させないと遷移後の状態保持がおかしくなる
                            launchSingleTop = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    }
                )
            }
            composable<Screen.Schedule> {
                ScheduleScreen(
                    goToReport = { shopId, shopName, menuName, iso8601Date ->
                        navController.navigate(Screen.Report(shopId, shopName, menuName, iso8601Date))
                    },
                    goToShop = { shopId, shopName ->
                        navController.navigate(Screen.Shop(shopId, shopName))
                    }
                )
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
            composable<Screen.History> {
                HistoryScreen(
                    reportId = historyReportId,
                    goToEditReport = { reportId ->
                        navController.navigate(Screen.EditReport(reportId))
                    },
                    clearReportId = {
                        historyReportId = null
                    }
                )
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
                        navController.navigate(Screen.Shop(shop.id, shop.name))
                    },
                    onAddShopClick = { areaName ->
                        navController.navigate(Screen.AddShop(areaName))
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
                ShopScreen(
                    shopId = screen.shopId,
                    shopName = screen.shopName,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = { editShop ->
                        navController.navigate(Screen.EditShop(editShop.toJsonString()))
                    },
                    onReportClick = { shop ->
                        navController.navigate(
                            Screen.Report(
                                shop.id,
                                shop.name,
                                shop.menuName1,
                                iso8601Date = shop.scheduledDate?.toString() ?: createTodayLocalDate().toString()
                            )
                        )
                    }
                )
            }
            composable<Screen.AddShop> { backStackEntry ->
                val screen: Screen.AddShop = backStackEntry.toRoute()
                AddShopScreen(
                    areaName = screen.areaName,
                    onBackClick = { navController.popBackStack() },
                    onCompleted = { navController.popBackStack() }
                )
            }
            composable<Screen.EditShop> { backStackEntry ->
                val screen: Screen.EditShop = backStackEntry.toRoute()
                val shop =
                    try {
                        ShopInfo.fromJsonString(screen.shopJson)
                    } catch (_: Exception) {
                        // エラーの場合はデフォルトのShopオブジェクトを作成
                        ShopInfo(
                            name = "エラー",
                            area = "",
                            shopUrl = "",
                            mapUrl = "",
                            star = 0,
                            stationName = "",
                            category = ""
                        )
                    }
                EditShopScreen(
                    shop = shop,
                    onBackClick = { navController.popBackStack() },
                    onCompleted = { navController.popBackStack() },
                    goToShopList = { areaName ->
                        navController.navigate(Screen.AreaShopList(areaName)) {
                            popUpTo(Screen.AreaShopList(areaName)) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable<Screen.Report> { backStackEntry ->
                val screen: Screen.Report = backStackEntry.toRoute()
                ReportScreen(
                    shopId = screen.shopId,
                    shopName = screen.shopName,
                    menuName = screen.menuName,
                    scheduledDate = LocalDate.parse(screen.iso8601Date),
                    onBackClick = { navController.popBackStack() },
                    goToHistory = {
                        navController.navigate(Screen.History) {
                            // タブクリック時と同じ処理で画面遷移させないと遷移後の状態保持がおかしくなる
                            launchSingleTop = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    }
                )
            }
            composable<Screen.EditReport> { backStackEntry ->
                val screen: Screen.EditReport = backStackEntry.toRoute()
                EditReportScreen(
                    reportId = screen.reportId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
