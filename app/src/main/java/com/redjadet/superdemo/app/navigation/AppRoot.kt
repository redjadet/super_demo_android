package com.redjadet.superdemo.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.redjadet.superdemo.features.dashboard.presentation.DashboardRoute
import com.redjadet.superdemo.features.dashboard.presentation.ProductionRisksRoute
import com.redjadet.superdemo.features.feed.presentation.FeedPostDetailRoute
import com.redjadet.superdemo.features.feed.presentation.FeedRoute
import com.redjadet.superdemo.features.items.presentation.ItemDetailRoute
import com.redjadet.superdemo.features.items.presentation.ItemsRoute

@Composable
fun AppRoot(
    initialDeepLink: AppDeepLink? = null,
    deepLinkConsumer: ((AppDeepLink?) -> Unit) -> Unit = {},
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(AppTab.Dashboard) }
    var pendingFeedPostId by remember { mutableStateOf<Int?>(null) }

    fun applyDeepLink(link: AppDeepLink?) {
        when (link) {
            null -> Unit
            AppDeepLink.Dashboard -> {
                selectedTab = AppTab.Dashboard
                navController.navigate(AppTab.Dashboard.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            AppDeepLink.ProductionRisks -> {
                selectedTab = AppTab.Dashboard
                navController.navigate(AppTab.Dashboard.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
                navController.navigate("dashboard/risks")
            }
            AppDeepLink.Items -> {
                selectedTab = AppTab.Items
                navController.navigate(AppTab.Items.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            AppDeepLink.Feed -> {
                selectedTab = AppTab.Feed
                navController.navigate(AppTab.Feed.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            is AppDeepLink.FeedPost -> {
                // Single navigation path: open Feed tab, let FeedRoute open detail once.
                selectedTab = AppTab.Feed
                pendingFeedPostId = link.id
                navController.navigate(AppTab.Feed.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

    LaunchedEffect(initialDeepLink) {
        applyDeepLink(initialDeepLink)
    }

    DisposableEffect(Unit) {
        deepLinkConsumer { link -> applyDeepLink(link) }
        onDispose { deepLinkConsumer { } }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        selectedTab = when {
            currentRoute?.startsWith("items") == true -> AppTab.Items
            currentRoute?.startsWith("feed") == true -> AppTab.Feed
            else -> AppTab.Dashboard
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    AppTab.Dashboard -> Icons.Filled.Checklist
                                    AppTab.Items -> Icons.AutoMirrored.Filled.List
                                    AppTab.Feed -> Icons.Filled.Forum
                                },
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                        modifier = Modifier.testTag("${tab.name.lowercase()}Tab"),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppTab.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AppTab.Dashboard.route) {
                DashboardRoute(
                    onOpenRisks = { navController.navigate("dashboard/risks") },
                )
            }
            composable("dashboard/risks") {
                ProductionRisksRoute(onBack = { navController.popBackStack() })
            }
            composable(AppTab.Items.route) {
                ItemsRoute(
                    onOpenItem = { id -> navController.navigate("items/$id") },
                )
            }
            composable(
                route = "items/{itemId}",
                arguments = listOf(navArgument("itemId") { type = NavType.LongType }),
            ) { entry ->
                val id = entry.arguments?.getLong("itemId") ?: return@composable
                ItemDetailRoute(
                    itemId = id,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(AppTab.Feed.route) {
                FeedRoute(
                    pendingPostId = pendingFeedPostId,
                    onPendingConsumed = { pendingFeedPostId = null },
                    onOpenPost = { id -> navController.navigate("feed/$id") },
                )
            }
            composable(
                route = "feed/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.IntType }),
            ) { entry ->
                val id = entry.arguments?.getInt("postId") ?: return@composable
                // Share FeedViewModel with the Feed tab so detail reads Room-backed posts.
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(AppTab.Feed.route)
                }
                FeedPostDetailRoute(
                    postId = id,
                    onBack = { navController.popBackStack() },
                    viewModel = hiltViewModel(parentEntry),
                )
            }
        }
    }
}
