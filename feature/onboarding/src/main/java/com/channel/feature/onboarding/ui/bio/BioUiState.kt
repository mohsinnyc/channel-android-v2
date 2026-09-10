package com.channel.feature.onboarding.ui.bio

import com.channel.core.network.api.NetworkError

data class BioUiState(
    val bio: String = "",
    val isSubmitting: Boolean = false,
    val submitError: NetworkError? = null,
) {
    val remainingCharacters: Int get() = MAX_LENGTH - bio.length

    companion object {
        // Mirrors channel-service's SubmitBioDto MaxLength(500) exactly (onboarding/onboarding.dto.ts).
        const val MAX_LENGTH = 500
    }
}
