package com.channel.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    signupDestination(navController)
    loginDestination(navController)
    forgotPasswordDestination(navController)
    resetPasswordDestination(navController)
}
