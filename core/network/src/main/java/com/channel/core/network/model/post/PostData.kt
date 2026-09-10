package com.channel.core.network.model.post

import kotlinx.serialization.Serializable

/**
 * Top-level post returned by the API (mirrors PostReadService.hydrate() in channel-service).
 *
 * - For REPOST, body fields (content/image/audio*) are null — `referencedPost` carries the content.
 * - For QUOTE, body fields are the quoter's own content; `referencedPost` holds the quoted post.
 * - For POST (original), body fields describe the content directly; `referencedPost` is null.
 *
 * Two deliberate deviations from the Channel-Android (v1) model this mirrors:
 * `createdAt` is nullable here (the backend's hydrate() can genuinely return null for it), and
 * the count fields are non-null (the backend always fills them, so callers don't need `?: 0`
 * scattered everywhere).
 */
@Serializable
data class PostData(
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

    val referencedPost: ReferencedPostData? = null,

    val isLiked: Boolean? = null,
    val isRepostedByUser: Boolean? = null,
    val isOwnPost: Boolean? = null,

    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val repostCount: Int = 0,
    val audioPlayCount: Int = 0,
)
