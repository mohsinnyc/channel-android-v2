package com.channel.feature.profile.data

import kotlinx.serialization.Serializable

/** Mirrors ProfilesService.get()'s response shape (profiles/profiles.service.ts). */
@Serializable
data class ProfileResponse(
    val userId: String,
    val username: String,
    val isVerified: Boolean,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val audioBioUrl: String,
    val audioBioPlayCount: Int,
    val listenerCount: Int,
    val listeningCount: Int,
    val isPrivate: Boolean,
    val posts: List<PostSummary>,
    val nextPageKey: String? = null,
)
