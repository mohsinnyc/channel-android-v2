package com.channel.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.channel.R
import com.channel.core.network.model.OnboardingState

@Composable
fun OnboardingPlaceholderDestination(state: OnboardingState) =
    PlaceholderContent(stringResource(R.string.destination_placeholder_onboarding, state.name))
