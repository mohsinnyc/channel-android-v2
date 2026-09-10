package com.channel.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.channel.feature.auth.navigation.SignupDestination
import com.channel.feature.auth.navigation.authNavGraph

/** Owns its own back stack, independent of whatever mode the app was in before. */
@Composable
fun AuthDestinationHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = SignupDestination) {
        authNavGraph(navController)
    }
}
