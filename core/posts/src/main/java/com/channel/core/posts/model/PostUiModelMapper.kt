package com.channel.core.posts.model

import com.channel.core.audio.playback.MediaIds
import com.channel.core.network.model.post.PostData
import com.channel.core.network.model.post.PostStatus
import com.channel.core.network.model.post.PostType
import com.channel.core.network.model.post.QuotedPostData
import com.channel.core.network.model.post.ReferencedPostData

/**
 * Maps the wire model to what a post row actually renders.
 *
 * - REPOST: the wrapper carries no content of its own — body comes from
 *   `referencedPost`, and a REPOST-of-a-QUOTE surfaces the innermost quoted
 *   post as the embedded block (one level of nesting, never deeper).
 * - QUOTE: body is the quoter's own content; `referencedPost` is the embedded
 *   quoted post.
 * - A MODERATED/DELETED post (at any level) becomes a tombstone rather than
 *   being dropped from the list, so the row still occupies its place.
 */
fun PostData.toUiModel(): PostUiModel {
    unavailableReason(postStatus)?.let { return PostUiModel.Unavailable(postId, it) }

    return when (postType) {
        PostType.REPOST -> {
            val referenced = referencedPost ?: return PostUiModel.Unavailable(postId, UnavailableReason.DELETED)
            val referencedUnavailable = unavailableReason(referenced.postStatus)
            if (referencedUnavailable != null) {
                PostUiModel.Unavailable(postId, referencedUnavailable)
            } else {
                PostUiModel.Available(
                    postId = postId,
                    viewKind = PostViewKind.REPOST,
                    reposterUsername = username,
                    body = referenced.toBodyUiModel(),
                    embedded = referenced.quotedPost?.toEmbeddedUiModel(),
                    likeCount = referenced.likeCount ?: 0,
                    commentCount = referenced.commentCount ?: 0,
                    repostCount = referenced.repostCount ?: 0,
                    isLiked = referenced.isLiked ?: false,
                    isRepostedByUser = referenced.isRepostedByUser ?: false,
                    isOwnPost = referenced.isOwnPost ?: false,
                )
            }
        }
        else -> PostUiModel.Available(
            postId = postId,
            viewKind = if (postType == PostType.QUOTE) PostViewKind.QUOTE else PostViewKind.ORIGINAL,
            reposterUsername = null,
            body = toBodyUiModel(),
            embedded = referencedPost?.toEmbeddedUiModel(),
            likeCount = likeCount,
            commentCount = commentCount,
            repostCount = repostCount,
            isLiked = isLiked ?: false,
            isRepostedByUser = isRepostedByUser ?: false,
            isOwnPost = isOwnPost ?: false,
        )
    }
}

private fun unavailableReason(status: PostStatus?): UnavailableReason? = when (status) {
    PostStatus.DELETED -> UnavailableReason.DELETED
    PostStatus.MODERATED -> UnavailableReason.MODERATED
    else -> null
}

private fun PostData.toBodyUiModel(): PostBodyUiModel = PostBodyUiModel(
    postId = postId,
    username = username,
    profileImageUrl = profileImageUrl,
    isVerified = isVerified ?: false,
    content = content,
    imageUrl = imageUrl,
    audio = playableAudio(postId, audioUrl, audioDurationMs, audioPlayCount),
    createdAt = createdAt,
)

private fun ReferencedPostData.toBodyUiModel(): PostBodyUiModel = PostBodyUiModel(
    postId = postId,
    username = username,
    profileImageUrl = profileImageUrl,
    isVerified = isVerified ?: false,
    content = content,
    imageUrl = imageUrl,
    audio = playableAudio(postId, audioUrl, audioDurationMs, audioPlayCount ?: 0),
    createdAt = createdAt,
)

private fun QuotedPostData.toBodyUiModel(): PostBodyUiModel = PostBodyUiModel(
    postId = postId,
    username = username,
    profileImageUrl = profileImageUrl,
    isVerified = isVerified ?: false,
    content = content,
    imageUrl = imageUrl,
    audio = playableAudio(postId, audioUrl, audioDurationMs, audioPlayCount ?: 0),
    createdAt = createdAt,
)

private fun ReferencedPostData.toEmbeddedUiModel(): EmbeddedUiModel =
    unavailableReason(postStatus)?.let { EmbeddedUiModel.Unavailable(it) } ?: EmbeddedUiModel.Available(toBodyUiModel())

private fun QuotedPostData.toEmbeddedUiModel(): EmbeddedUiModel =
    unavailableReason(postStatus)?.let { EmbeddedUiModel.Unavailable(it) } ?: EmbeddedUiModel.Available(toBodyUiModel())

private fun playableAudio(postId: String, url: String?, durationMs: Long?, playCount: Int): PlayableAudio? =
    url?.let { PlayableAudio(mediaId = MediaIds.post(postId), url = it, durationMs = durationMs, playCount = playCount) }
