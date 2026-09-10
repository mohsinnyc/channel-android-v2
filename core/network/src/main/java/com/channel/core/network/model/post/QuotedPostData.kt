package com.channel.core.network.model.post

import kotlinx.serialization.Serializable

/** The original post that was quoted inside a [ReferencedPostData]. Terminal layer — no further nesting. */
@Serializable
data class QuotedPostData(
    val postId: String,
    val userId: String? = null,
    val username: String? = null,
    val isVerified: Boolean? = null,
    val profileImageUrl: String? = null,

    val content: String? = null,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val audioDurationMs: Long? = null,
    val audioPlayCount: Int? = null,
    val mentions: List<MentionData>? = null,
    val createdAt: String? = null,

    val postType: PostType? = null,
    val postStatus: PostStatus? = null,

    val category: PostCategoryData? = null,
)
