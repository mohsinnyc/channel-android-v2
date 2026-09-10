package com.channel.feature.profile.data

import kotlinx.serialization.Serializable

/** Mirrors the post shape PostReadService.hydrate() produces (posts/post-read.service.ts). */
@Serializable
data class PostSummary(
    val postId: String,
    val username: String? = null,
    val profileImageUrl: String? = null,
    val content: String? = null,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val audioDurationMs: Long? = null,
    val createdAt: String? = null,
    val likeCount: Int,
    val commentCount: Int,
    val repostCount: Int,
)
