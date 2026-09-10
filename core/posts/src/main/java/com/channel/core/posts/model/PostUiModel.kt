package com.channel.core.posts.model

enum class PostViewKind { ORIGINAL, REPOST, QUOTE }

enum class UnavailableReason { DELETED, MODERATED }

data class PlayableAudio(
    val mediaId: String,
    val url: String,
    val durationMs: Long?,
    val playCount: Int,
)

data class PostBodyUiModel(
    val postId: String,
    val username: String?,
    val profileImageUrl: String?,
    val isVerified: Boolean,
    val content: String?,
    val imageUrl: String?,
    val audio: PlayableAudio?,
    val createdAt: String?,
)

sealed interface EmbeddedUiModel {
    data class Available(val body: PostBodyUiModel) : EmbeddedUiModel
    data class Unavailable(val reason: UnavailableReason) : EmbeddedUiModel
}

/** What a single row in a post list renders — a real post, or a tombstone for a removed one. */
sealed interface PostUiModel {
    val postId: String

    data class Available(
        override val postId: String,
        val viewKind: PostViewKind,
        /** Only set for REPOST — who reposted it. */
        val reposterUsername: String?,
        val body: PostBodyUiModel,
        /** The quoted post, only for QUOTE (and for a REPOST of a QUOTE). */
        val embedded: EmbeddedUiModel?,
        val likeCount: Int,
        val commentCount: Int,
        val repostCount: Int,
        val isLiked: Boolean,
        val isRepostedByUser: Boolean,
        val isOwnPost: Boolean,
    ) : PostUiModel

    data class Unavailable(
        override val postId: String,
        val reason: UnavailableReason,
    ) : PostUiModel
}
