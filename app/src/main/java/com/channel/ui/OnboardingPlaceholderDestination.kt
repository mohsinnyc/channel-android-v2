package com.channel.ui

import androidx.compose.runtime.Composable
import com.channel.core.network.model.OnboardingState

@Composable
fun OnboardingPlaceholderDestination(state: OnboardingState) = PlaceholderContent("Onboarding: $state — TODO")
