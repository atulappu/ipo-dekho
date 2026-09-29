package com.example.ipotracker.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ipotracker.di.AppContainer
import com.example.ipotracker.presentation.allotment.AllotmentScreen
import com.example.ipotracker.presentation.allotment.AllotmentViewModel
import com.example.ipotracker.presentation.applicationinfo.IpoApplicationInfoScreen
import com.example.ipotracker.presentation.calculator.IpoCalculatorScreen
import com.example.ipotracker.presentation.calculator.IpoCalculatorViewModel
import com.example.ipotracker.presentation.calendar.IpoCalendarScreen
import com.example.ipotracker.presentation.calendar.IpoCalendarViewModel
import com.example.ipotracker.presentation.comparison.IpoComparisonScreen
import com.example.ipotracker.presentation.comparison.IpoComparisonViewModel
import com.example.ipotracker.presentation.detail.IpoDetailScreen
import com.example.ipotracker.presentation.detail.IpoDetailViewModel
import com.example.ipotracker.presentation.gmp.GmpScreen
import com.example.ipotracker.presentation.gmp.GmpViewModel
import com.example.ipotracker.presentation.home.HomeScreen
import com.example.ipotracker.presentation.home.HomeViewModel
import com.example.ipotracker.presentation.ipo.IpoListScreen
import com.example.ipotracker.presentation.ipo.IpoListViewModel
import com.example.ipotracker.presentation.livemarket.LiveMarketScreen
import com.example.ipotracker.presentation.more.DataSourcesScreen
import com.example.ipotracker.presentation.more.DisclaimerScreen
import com.example.ipotracker.presentation.more.MoreScreen
import com.example.ipotracker.presentation.more.SettingsScreen
import com.example.ipotracker.presentation.search.SearchScreen
import com.example.ipotracker.presentation.search.SearchViewModel
import com.example.ipotracker.presentation.subscription.SubscriptionScreen
import com.example.ipotracker.presentation.subscription.SubscriptionViewModel
import com.example.ipotracker.presentation.watchlist.WatchlistScreen
import com.example.ipotracker.presentation.watchlist.WatchlistViewModel
import androidx.compose.ui.graphics.Color
import com.example.ipotracker.presentation.aichat.AiChatScreen
import com.example.ipotracker.presentation.aichat.AiChatViewModel
import com.example.ipotracker.presentation.auth.AuthScreen
import com.example.ipotracker.presentation.voicelive.VoiceLiveScreen
import com.example.ui.theme.*

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CircularAiAssistantFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val tooltipState = rememberTooltipState(isPersistent = false)

    LaunchedEffect(isHovered) {
        if (isHovered) {
            tooltipState.show()
        } else {
            tooltipState.dismiss()
        }
    }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip(
                containerColor = Color(0xFF212121),
                contentColor = Color.White,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "ASK AI",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        },
        state = tooltipState
    ) {
        FloatingActionButton(
            onClick = onClick,
            interactionSource = interactionSource,
            shape = CircleShape,
            containerColor = PrimaryOrange,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp,
                hoveredElevation = 6.dp
            ),
            modifier = modifier
                .size(48.dp)
                .testTag("ask_ai_fab")
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "ASK AI",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun AppNavigation(
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("ipo_list?status={status}", "IPO", Icons.Filled.FormatListBulleted, Icons.Outlined.FormatListBulleted),
        BottomNavItem(Screen.Gmp.route, "GMP", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
        BottomNavItem(Screen.Watchlist.route, "Watchlist", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder),
        BottomNavItem(Screen.More.route, "More", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
    )

    val showBottomBar = bottomNavItems.any { item ->
        if (item.route.contains("?")) {
            currentRoute?.startsWith("ipo_list") == true
        } else {
            currentRoute == item.route
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (showBottomBar && currentRoute?.startsWith("ai_chat") != true && currentRoute?.startsWith("voice_live") != true) {
                CircularAiAssistantFab(
                    onClick = { navController.navigate(Screen.AiChat.createRoute(null)) },
                    modifier = Modifier.testTag("ask_ai_fab")
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                Column {
                    HorizontalDivider(color = BorderLight, thickness = 1.dp)
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = if (item.route.contains("?")) {
                                currentRoute?.startsWith("ipo_list") == true
                            } else {
                                currentRoute == item.route
                            }

                            NavigationBarItem(
                                alwaysShowLabel = true,
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(21.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                },
                                selected = isSelected,
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryOrange,
                                    selectedTextColor = PrimaryOrangeDark,
                                    indicatorColor = PrimaryOrangeLight,
                                    unselectedIconColor = NeutralGray,
                                    unselectedTextColor = NeutralGray
                                ),
                                onClick = {
                                    val target = if (item.route.contains("?")) "ipo_list" else item.route
                                    navController.navigate(target) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                modifier = Modifier.testTag("nav_item_${item.title.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.provideFactory(appContainer.ipoRepository)
                )
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { id -> navController.navigate(Screen.IpoDetail.createRoute(id)) },
                    onNavigateToIpoList = { status -> navController.navigate(Screen.IpoList.createRoute(status)) },
                    onNavigateToCalculator = { id -> navController.navigate(Screen.Calculator.createRoute(id)) },
                    onNavigateToAllotment = { id -> navController.navigate(Screen.Allotment.createRoute(id)) },
                    onNavigateToComparison = { navController.navigate(Screen.Comparison.route) },
                    onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToSubscription = { id -> navController.navigate(Screen.Subscription.createRoute(id)) },
                    onNavigateToApplicationInfo = { id -> navController.navigate(Screen.ApplicationInfo.createRoute(id)) },
                    onNavigateToLiveMarket = { id -> navController.navigate(Screen.LiveMarket.createRoute(id)) },
                    onNavigateToAiChat = { query -> navController.navigate(Screen.AiChat.createRoute(query)) },
                    onNavigateToVoiceLive = { topic -> navController.navigate(Screen.VoiceLive.createRoute(topic)) },
                    onNavigateToAuth = { navController.navigate(Screen.Auth.route) }
                )
            }

            // IPO List
            composable(
                route = Screen.IpoList.route,
                arguments = listOf(
                    navArgument("status") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val statusArg = backStackEntry.arguments?.getString("status")
                val viewModel: IpoListViewModel = viewModel(
                    factory = IpoListViewModel.provideFactory(appContainer.ipoRepository, statusArg)
                )
                IpoListScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { id -> navController.navigate(Screen.IpoDetail.createRoute(id)) },
                    onNavigateToCalculator = { id -> navController.navigate(Screen.Calculator.createRoute(id)) },
                    onNavigateToApplicationInfo = { id -> navController.navigate(Screen.ApplicationInfo.createRoute(id)) },
                    onNavigateToAllotment = { id -> navController.navigate(Screen.Allotment.createRoute(id)) },
                    onNavigateToLiveMarket = { id -> navController.navigate(Screen.LiveMarket.createRoute(id)) }
                )
            }

            // GMP Tracker
            composable(Screen.Gmp.route) {
                val viewModel: GmpViewModel = viewModel(
                    factory = GmpViewModel.provideFactory(appContainer.ipoRepository)
                )
                GmpScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { id -> navController.navigate(Screen.IpoDetail.createRoute(id)) },
                    onNavigateToCalculator = { id -> navController.navigate(Screen.Calculator.createRoute(id)) }
                )
            }

            // Watchlist
            composable(Screen.Watchlist.route) {
                val viewModel: WatchlistViewModel = viewModel(
                    factory = WatchlistViewModel.provideFactory(appContainer.ipoRepository)
                )
                WatchlistScreen(
                    viewModel = viewModel,
                    onNavigateToDetail = { id -> navController.navigate(Screen.IpoDetail.createRoute(id)) },
                    onNavigateToExplore = { navController.navigate(Screen.IpoList.createRoute(null)) },
                    onNavigateToApplicationInfo = { id -> navController.navigate(Screen.ApplicationInfo.createRoute(id)) },
                    onNavigateToAllotment = { id -> navController.navigate(Screen.Allotment.createRoute(id)) },
                    onNavigateToLiveMarket = { id -> navController.navigate(Screen.LiveMarket.createRoute(id)) }
                )
            }

            // More
            composable(Screen.More.route) {
                MoreScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToDisclaimer = { navController.navigate(Screen.Disclaimer.route) },
                    onNavigateToDataSources = { navController.navigate(Screen.DataSources.route) },
                    onNavigateToSubscription = { navController.navigate(Screen.Subscription.createRoute(null)) },
                    onNavigateToAiChat = { navController.navigate(Screen.AiChat.createRoute(null)) },
                    onNavigateToVoiceLive = { navController.navigate(Screen.VoiceLive.createRoute(null)) },
                    onNavigateToAuth = { navController.navigate(Screen.Auth.route) }
                )
            }

            // IPO Detail
            composable(
                route = Screen.IpoDetail.route,
                arguments = listOf(navArgument("ipoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val ipoId = backStackEntry.arguments?.getString("ipoId") ?: ""
                val viewModel: IpoDetailViewModel = viewModel(
                    factory = IpoDetailViewModel.provideFactory(appContainer.ipoRepository, ipoId)
                )
                IpoDetailScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCalculator = { id -> navController.navigate(Screen.Calculator.createRoute(id)) },
                    onNavigateToApplicationInfo = { id -> navController.navigate(Screen.ApplicationInfo.createRoute(id)) },
                    onNavigateToSubscription = { id -> navController.navigate(Screen.Subscription.createRoute(id)) },
                    onNavigateToAllotment = { id -> navController.navigate(Screen.Allotment.createRoute(id)) }
                )
            }

            // IPO Calculator
            composable(
                route = Screen.Calculator.route,
                arguments = listOf(
                    navArgument("ipoId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val ipoId = backStackEntry.arguments?.getString("ipoId")
                val viewModel: IpoCalculatorViewModel = viewModel(
                    factory = IpoCalculatorViewModel.provideFactory(appContainer.ipoRepository, ipoId)
                )
                IpoCalculatorScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Allotment Status
            composable(
                route = Screen.Allotment.route,
                arguments = listOf(
                    navArgument("ipoId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val ipoId = backStackEntry.arguments?.getString("ipoId")
                val viewModel: AllotmentViewModel = viewModel(
                    factory = AllotmentViewModel.provideFactory(appContainer.ipoRepository, ipoId)
                )
                AllotmentScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // IPO Comparison
            composable(Screen.Comparison.route) {
                val viewModel: IpoComparisonViewModel = viewModel(
                    factory = IpoComparisonViewModel.provideFactory(appContainer.ipoRepository)
                )
                IpoComparisonScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // IPO Calendar
            composable(Screen.Calendar.route) {
                val viewModel: IpoCalendarViewModel = viewModel(
                    factory = IpoCalendarViewModel.provideFactory(appContainer.ipoRepository)
                )
                IpoCalendarScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { id -> navController.navigate(Screen.IpoDetail.createRoute(id)) }
                )
            }

            // Search
            composable(Screen.Search.route) {
                val viewModel: SearchViewModel = viewModel(
                    factory = SearchViewModel.provideFactory(appContainer.ipoRepository)
                )
                SearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { id -> navController.navigate(Screen.IpoDetail.createRoute(id)) },
                    onNavigateToApplicationInfo = { id -> navController.navigate(Screen.ApplicationInfo.createRoute(id)) },
                    onNavigateToAllotment = { id -> navController.navigate(Screen.Allotment.createRoute(id)) },
                    onNavigateToLiveMarket = { id -> navController.navigate(Screen.LiveMarket.createRoute(id)) }
                )
            }

            // Application Info
            composable(
                route = Screen.ApplicationInfo.route,
                arguments = listOf(navArgument("ipoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val ipoId = backStackEntry.arguments?.getString("ipoId") ?: ""
                IpoApplicationInfoScreen(
                    repository = appContainer.ipoRepository,
                    ipoId = ipoId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(onNavigateBack = { navController.popBackStack() })
            }

            // Disclaimer
            composable(Screen.Disclaimer.route) {
                DisclaimerScreen(onNavigateBack = { navController.popBackStack() })
            }

            // Data Sources
            composable(Screen.DataSources.route) {
                DataSourcesScreen(onNavigateBack = { navController.popBackStack() })
            }

            // IPO Subscription Details & Trends
            composable(
                route = Screen.Subscription.route,
                arguments = listOf(
                    navArgument("ipoId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val ipoId = backStackEntry.arguments?.getString("ipoId")
                val viewModel: SubscriptionViewModel = viewModel(
                    factory = SubscriptionViewModel.provideFactory(appContainer.ipoRepository, ipoId)
                )
                SubscriptionScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { id -> navController.navigate(Screen.IpoDetail.createRoute(id)) }
                )
            }

            // Live Market Data for Officially Listed IPOs
            composable(
                route = Screen.LiveMarket.route,
                arguments = listOf(navArgument("ipoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val ipoId = backStackEntry.arguments?.getString("ipoId") ?: ""
                LiveMarketScreen(
                    repository = appContainer.ipoRepository,
                    ipoId = ipoId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToDetail = { id -> navController.navigate(Screen.IpoDetail.createRoute(id)) }
                )
            }

            // Gemini Multi-turn AI Chatbot
            composable(
                route = Screen.AiChat.route,
                arguments = listOf(
                    navArgument("initialQuery") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val query = backStackEntry.arguments?.getString("initialQuery")
                val viewModel: AiChatViewModel = viewModel(
                    factory = AiChatViewModel.provideFactory(
                        appContainer.geminiService,
                        appContainer.firebaseAuthService,
                        appContainer.firestoreService,
                        query
                    )
                )
                AiChatScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToVoiceLive = { topic ->
                        navController.navigate(Screen.VoiceLive.createRoute(topic))
                    }
                )
            }

            // Real-Time Voice Conversations (Gemini Live API)
            composable(
                route = Screen.VoiceLive.route,
                arguments = listOf(
                    navArgument("initialTopic") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val topic = backStackEntry.arguments?.getString("initialTopic")
                VoiceLiveScreen(
                    geminiService = appContainer.geminiService,
                    initialTopic = topic,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Firebase Auth & Cloud Firestore Sync
            composable(Screen.Auth.route) {
                AuthScreen(
                    authService = appContainer.firebaseAuthService,
                    firestoreService = appContainer.firestoreService,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
