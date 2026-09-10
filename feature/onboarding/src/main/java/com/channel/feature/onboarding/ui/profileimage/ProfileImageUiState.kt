package com.channel.feature.onboarding.ui.profileimage

import com.channel.core.network.api.NetworkError
import java.io.File

data class ProfileImageUiState(
    val imageFile: File? = null,
    val isProcessingPick: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitError: NetworkError? = null,
) {
    val canContinue: Boolean get() = imageFile != null && !isSubmitting && !isProcessingPick
}
