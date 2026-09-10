package com.channel.core.audio.playback

/**
 * The one place every feature builds an [AudioPlayer] mediaId, so the same
 * piece of content always gets the same id wherever it's rendered — never
 * screen-scoped, so a post playing in one list shows as playing in another
 * list of the same content too.
 */
object MediaIds {
    fun post(postId: String): String = "post:$postId"
    fun comment(commentId: String): String = "comment:$commentId"
    fun reply(replyId: String): String = "reply:$replyId"
    fun profileAudio(userId: String): String = "profileAudio:$userId"
}
