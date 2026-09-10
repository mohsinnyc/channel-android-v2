package com.channel.core.network.model.post

import kotlinx.serialization.Serializable

/**
 * The post being referenced by a QUOTE or REPOST.
 * REPOST is unwrapped server-side and never appears here — only POST or QUOTE do.
 */
@Serializable
data class ReferencedPostData(
    val postId: String,
    val userId: String? = null,
    val username: String? = null,
    val isVerified: Boolean? = null,
    val profileImageUrl: String? = null,

    val content: String? = null,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val audioDurationMs: Long? = null,
    val mentions: List<MentionData>? = null,
    val createdAt: String? = null,

    val postType: PostType? = null,
    val postStatus: PostStatus? = null,

    val category: PostCategoryData? = null,

    /** Only present when this referenced post is itself a QUOTE. */
    val quotedPost: QuotedPostData? = null,

    val isLiked: Boolean? = null,
    val isRepostedByUser: Boolean? = null,
    val isOwnPost: Boolean? = null,

    val likeCount: Int? = null,
    val commentCount: Int? = null,
    val repostCount: Int? = null,
    val audioPlayCount: Int? = null,
)
