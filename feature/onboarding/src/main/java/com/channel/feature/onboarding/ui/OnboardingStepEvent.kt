package com.channel.feature.onboarding.ui

/** One-shot "this step is done, move to the next one" signal — shared by every step. */
sealed interface OnboardingStepEvent {
    data object Continue : OnboardingStepEvent
}
