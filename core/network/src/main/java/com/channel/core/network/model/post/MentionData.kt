package com.channel.core.network.model.post

import kotlinx.serialization.Serializable

/** A resolved mention returned by the backend in post/comment responses. */
@Serializable
data class MentionData(
    val userId: String,
    val username: String,
    val startIndex: Int,
    val endIndex: Int,
)
