package com.channel.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.channel.R
import com.channel.core.contracts.navigation.ChannelDestination
import com.channel.feature.profile.navigation.ProfileDestination
import com.channel.feature.profile.navigation.profileDestination
import com.channel.navigation.ExploreDestination
import com.channel.navigation.FeedDestination
import com.channel.navigation.NotificationsDestination
import com.channel.navigation.exploreDestination
import com.channel.navigation.feedDestination
import com.channel.navigation.notificationsDestination
import kotlin.reflect.KClass

private data class MainTab(
    val destination: ChannelDestination,
    val destinationClass: KClass<out ChannelDestination>,
    val icon: ImageVector,
    val labelRes: Int,
)

private val mainTabs = listOf(
    MainTab(FeedDestination, FeedDestination::class, Icons.Filled.Home, R.string.main_tab_feed),
    MainTab(ExploreDestination, ExploreDestination::class, Icons.Filled.Explore, R.string.main_tab_explore),
    MainTab(NotificationsDestination, NotificationsDestination::class, Icons.Filled.Notifications, R.string.main_tab_notifications),
    MainTab(ProfileDestination, ProfileDestination::class, Icons.Filled.Person, R.string.main_tab_profile),
)

/** Owns the bottom-nav shell. Each tab keeps its own back stack/state across switches. */
@Composable
fun MainDestinationHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                mainTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.hasRoute(tab.destinationClass) } == true,
                        onClick = {
                            navController.navigate(tab.destination) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = stringResource(tab.labelRes)) },
                        label = { Text(stringResource(tab.labelRes)) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ProfileDestination,
            modifier = Modifier.padding(padding),
        ) {
            feedDestination()
            exploreDestination()
            notificationsDestination()
            profileDestination()
        }
    }
}
