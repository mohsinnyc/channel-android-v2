package com.channel.navigation

import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.channel.R
import com.channel.ui.PlaceholderContent

fun NavGraphBuilder.exploreDestination() {
    composable<ExploreDestination> {
        PlaceholderContent(stringResource(R.string.destination_placeholder_explore))
    }
}
