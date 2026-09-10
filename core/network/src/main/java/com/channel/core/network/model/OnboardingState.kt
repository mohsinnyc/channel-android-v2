package com.channel.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Mirrors channel-service's OnboardingState enum (database/entities.ts) exactly. */
@Serializable
enum class OnboardingState {
    @SerialName("New_User") NewUser,
    @SerialName("Profile_Image") ProfileImage,
    @SerialName("Profile_Bio") Bio,
    @SerialName("Profile_Audio") Audio,
    @SerialName("Profile_Interests") Interests,
    @SerialName("OnBoarding_Complete") Complete,
}
