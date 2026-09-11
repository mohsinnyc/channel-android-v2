package com.channel.feature.profile.data

import com.channel.core.network.model.post.MentionData
import com.channel.core.network.model.post.PostData
import com.channel.core.network.model.post.PostStatus
import com.channel.core.network.model.post.PostType
import com.channel.core.network.model.post.QuotedPostData
import com.channel.core.network.model.post.ReferencedPostData

/**
 * TEMPORARY — UI validation only. Real accounts have no posts/follows yet
 * since the follow flow isn't built, so there's no other way to see the
 * repost/quote/tombstone/audio render paths on a real device right now.
 * Delete this file and flip ProfileRepository.USE_MOCK_DATA back to false
 * once there's real content to look at instead.
 */
object MockProfileData {

    private val avatar1 = "https://picsum.photos/seed/mia/200"
    private val avatar2 = "https://picsum.photos/seed/jordan/200"
    private val avatar3 = "https://picsum.photos/seed/sam/200"
    private val postImage = "https://picsum.photos/seed/rooftop/800/450"
    private val audio1 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    private val audio2 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
    private val audio3 = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"

    fun sample(): ProfileResponse = ProfileResponse(
        userId = "mock-user-1",
        username = "mia.chen",
        isVerified = true,
        bio = "Daily voice notes about music, travel, and terrible puns. Say hi.",
        profileImageUrl = avatar1,
        audioBioUrl = audio1,
        audioBioPlayCount = 3123,
        listenerCount = 1204,
        listeningCount = 312,
        isPrivate = false,
        posts = listOf(
            textOnlyPost(),
            imagePost(),
            audioPost(),
            quotePostWithAudio(),
            repostOfAudioPost(),
            repostOfQuote(),
            quoteOfModeratedPost(),
            deletedPost(),
        ),
    )

    private fun textOnlyPost() = PostData(
        postId = "post-text",
        userId = "mock-user-1",
        username = "mia.chen",
        isVerified = true,
        profileImageUrl = avatar1,
        content = "Recorded this on a rooftop at sunset - the wind kept sneaking into the mic.",
        createdAt = minutesAgo(20),
        postType = PostType.POST,
        postStatus = PostStatus.AVAILABLE,
        likeCount = 86,
        commentCount = 14,
        repostCount = 5,
    )

    private fun imagePost() = PostData(
        postId = "post-image",
        userId = "mock-user-1",
        username = "mia.chen",
        isVerified = true,
        profileImageUrl = avatar1,
        content = "New favorite corner of the city.",
        imageUrl = postImage,
        createdAt = hoursAgo(4),
        postType = PostType.POST,
        postStatus = PostStatus.AVAILABLE,
        likeCount = 203,
        commentCount = 31,
        repostCount = 9,
    )

    private fun audioPost() = PostData(
        postId = "post-audio",
        userId = "mock-user-1",
        username = "mia.chen",
        isVerified = true,
        profileImageUrl = avatar1,
        content = "A little instrumental sketch from this morning.",
        audioUrl = audio2,
        audioDurationMs = 60_000L,
        audioPlayCount = 512,
        mentions = listOf(MentionData(userId = "mock-user-2", username = "jordan.k", startIndex = 0, endIndex = 0)),
        createdAt = hoursAgo(9),
        postType = PostType.POST,
        postStatus = PostStatus.AVAILABLE,
        likeCount = 47,
        commentCount = 6,
        repostCount = 2,
    )

    private fun quotePostWithAudio() = PostData(
        postId = "post-quote",
        userId = "mock-user-1",
        username = "mia.chen",
        isVerified = true,
        profileImageUrl = avatar1,
        content = "This one's been stuck in my head all week.",
        createdAt = daysAgo(1),
        postType = PostType.QUOTE,
        postStatus = PostStatus.AVAILABLE,
        referencedPost = ReferencedPostData(
            postId = "post-quoted-original",
            userId = "mock-user-2",
            username = "jordan.k",
            isVerified = false,
            profileImageUrl = avatar2,
            content = "Late-night jam session, unedited.",
            audioUrl = audio3,
            audioDurationMs = 78_000L,
            audioPlayCount = 1_204,
            createdAt = daysAgo(2),
            postType = PostType.POST,
            postStatus = PostStatus.AVAILABLE,
            likeCount = 340,
            commentCount = 52,
            repostCount = 18,
        ),
        likeCount = 61,
        commentCount = 9,
        repostCount = 3,
    )

    private fun repostOfAudioPost() = PostData(
        postId = "post-repost",
        userId = "mock-user-1",
        username = "mia.chen",
        createdAt = daysAgo(3),
        postType = PostType.REPOST,
        postStatus = PostStatus.AVAILABLE,
        referencedPost = ReferencedPostData(
            postId = "post-reposted-original",
            userId = "mock-user-3",
            username = "sam.rivera",
            isVerified = true,
            profileImageUrl = avatar3,
            content = "Field recording from the farmers market this morning.",
            audioUrl = audio1,
            audioDurationMs = 45_000L,
            audioPlayCount = 2_890,
            createdAt = daysAgo(3),
            postType = PostType.POST,
            postStatus = PostStatus.AVAILABLE,
            likeCount = 512,
            commentCount = 88,
            repostCount = 40,
        ),
    )

    /** A repost of a quote — exercises the one-level-deep embedded-quote-inside-a-repost path. */
    private fun repostOfQuote() = PostData(
        postId = "post-repost-of-quote",
        userId = "mock-user-1",
        username = "mia.chen",
        createdAt = daysAgo(5),
        postType = PostType.REPOST,
        postStatus = PostStatus.AVAILABLE,
        referencedPost = ReferencedPostData(
            postId = "post-quote-being-reposted",
            userId = "mock-user-2",
            username = "jordan.k",
            profileImageUrl = avatar2,
            content = "Been listening to this on repeat.",
            createdAt = daysAgo(5),
            postType = PostType.QUOTE,
            postStatus = PostStatus.AVAILABLE,
            quotedPost = QuotedPostData(
                postId = "post-innermost-quoted",
                userId = "mock-user-3",
                username = "sam.rivera",
                isVerified = true,
                profileImageUrl = avatar3,
                content = "Original voice memo, three takes to get right.",
                audioUrl = audio2,
                audioDurationMs = 33_000L,
                audioPlayCount = 976,
                createdAt = daysAgo(6),
                postType = PostType.POST,
                postStatus = PostStatus.AVAILABLE,
            ),
            likeCount = 120,
            commentCount = 15,
            repostCount = 7,
        ),
    )

    /** A quote whose quoted post was since moderated — exercises the embedded tombstone path. */
    private fun quoteOfModeratedPost() = PostData(
        postId = "post-quote-of-moderated",
        userId = "mock-user-1",
        username = "mia.chen",
        isVerified = true,
        profileImageUrl = avatar1,
        content = "Wow, did not expect that reaction.",
        createdAt = daysAgo(8),
        postType = PostType.QUOTE,
        postStatus = PostStatus.AVAILABLE,
        referencedPost = ReferencedPostData(
            postId = "post-now-moderated",
            postType = PostType.POST,
            postStatus = PostStatus.MODERATED,
        ),
        likeCount = 4,
        commentCount = 2,
        repostCount = 0,
    )

    /** A top-level deleted post — exercises the full-row tombstone path. */
    private fun deletedPost() = PostData(
        postId = "post-deleted",
        postType = PostType.POST,
        postStatus = PostStatus.DELETED,
        createdAt = daysAgo(10),
    )

    private fun minutesAgo(minutes: Long) = java.time.Instant.now().minusSeconds(minutes * 60).toString()
    private fun hoursAgo(hours: Long) = java.time.Instant.now().minusSeconds(hours * 3600).toString()
    private fun daysAgo(days: Long) = java.time.Instant.now().minusSeconds(days * 86_400).toString()
}
