package com.channel.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.channel.feature.auth.navigation.EmailVerificationDestination
import com.channel.feature.auth.navigation.emailVerificationDestination

/**
 * Owns its own NavHost so EmailVerificationViewModel is scoped to a
 * NavBackStackEntry instead of falling back to the Activity's ViewModelStore.
 * Without this, the same ViewModel instance (and its stale input) would
 * survive a logout and a brand-new signup, since it would otherwise live for
 * the entire process.
 */
@Composable
fun EmailVerificationDestinationHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = EmailVerificationDestination) {
        emailVerificationDestination()
    }
}
