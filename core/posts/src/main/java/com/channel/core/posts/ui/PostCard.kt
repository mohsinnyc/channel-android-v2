package com.channel.core.posts.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.channel.core.designsystem.components.Avatar
import com.channel.core.designsystem.theme.Spacing
import com.channel.core.posts.R
import com.channel.core.posts.model.EmbeddedUiModel
import com.channel.core.posts.model.PlayableAudio
import com.channel.core.posts.model.PostBodyUiModel
import com.channel.core.posts.model.PostUiModel
import com.channel.core.posts.model.PostViewKind
import com.channel.core.posts.model.UnavailableReason

/** The one post card used everywhere a post is rendered — feed, profile, hashtag, search, comments. */
@Composable
fun PostCard(
    post: PostUiModel,
    isAudioPlaying: Boolean,
    isEmbeddedAudioPlaying: Boolean,
    onToggleAudio: (PlayableAudio) -> Unit,
    onToggleEmbeddedAudio: (PlayableAudio) -> Unit,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onRepostClick: () -> Unit = {},
) {
    when (post) {
        is PostUiModel.Unavailable -> PostTombstoneCard(post.reason, modifier)
        is PostUiModel.Available -> PostAvailableCard(
            post = post,
            isAudioPlaying = isAudioPlaying,
            isEmbeddedAudioPlaying = isEmbeddedAudioPlaying,
            onToggleAudio = onToggleAudio,
            onToggleEmbeddedAudio = onToggleEmbeddedAudio,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onRepostClick = onRepostClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun PostAvailableCard(
    post: PostUiModel.Available,
    isAudioPlaying: Boolean,
    isEmbeddedAudioPlaying: Boolean,
    onToggleAudio: (PlayableAudio) -> Unit,
    onToggleEmbeddedAudio: (PlayableAudio) -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onRepostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Spacing.m),
        ) {
            if (post.viewKind == PostViewKind.REPOST && post.reposterUsername != null) {
                Text(
                    stringResource(R.string.post_reposted_by, post.reposterUsername),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            PostHeaderRow(post.body)

            post.body.content?.let { content ->
                Text(content, style = MaterialTheme.typography.bodyMedium)
            }

            post.body.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            }

            post.body.audio?.let { audio ->
                PostAudioRow(audio = audio, isPlaying = isAudioPlaying, onToggle = { onToggleAudio(audio) })
            }

            when (val embedded = post.embedded) {
                is EmbeddedUiModel.Available -> EmbeddedQuoteBox(
                    body = embedded.body,
                    isAudioPlaying = isEmbeddedAudioPlaying,
                    onToggleAudio = onToggleEmbeddedAudio,
                )
                is EmbeddedUiModel.Unavailable -> EmbeddedQuoteTombstone(embedded.reason)
                null -> Unit
            }

            PostActionsRow(
                likeCount = post.likeCount,
                commentCount = post.commentCount,
                repostCount = post.repostCount,
                onLikeClick = onLikeClick,
                onCommentClick = onCommentClick,
                onRepostClick = onRepostClick,
            )
        }
    }
}

@Composable
private fun PostTombstoneCard(reason: UnavailableReason, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Text(
            text = stringResource(
                if (reason == UnavailableReason.DELETED) R.string.post_deleted else R.string.post_moderated
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(Spacing.l),
        )
    }
}

@Composable
private fun PostHeaderRow(body: PostBodyUiModel) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.s)) {
        Avatar(imageUrl = body.profileImageUrl, fallbackText = body.username.orEmpty(), size = 32.dp)
        Text(body.username.orEmpty(), style = MaterialTheme.typography.labelLarge)
        relativeTimeLabel(body.createdAt)?.let { time ->
            Text("· $time", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PostAudioRow(audio: PlayableAudio, isPlaying: Boolean, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.s),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = Spacing.m, vertical = Spacing.s),
    ) {
        IconButton(
            onClick = onToggle,
            modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = stringResource(if (isPlaying) R.string.post_pause_audio else R.string.post_play_audio),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp),
            )
        }
        audio.durationMs?.let { duration ->
            Text(formatDuration(duration), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Icon(
                Icons.Filled.Hearing,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp),
            )
            Text(formatPlayCount(audio.playCount), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmbeddedQuoteBox(body: PostBodyUiModel, isAudioPlaying: Boolean, onToggleAudio: (PlayableAudio) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(Spacing.m),
        verticalArrangement = Arrangement.spacedBy(Spacing.s),
    ) {
        PostHeaderRow(body)
        body.content?.let { content -> Text(content, style = MaterialTheme.typography.bodyMedium) }
        body.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(10.dp)),
            )
        }
        body.audio?.let { audio ->
            PostAudioRow(audio = audio, isPlaying = isAudioPlaying, onToggle = { onToggleAudio(audio) })
        }
    }
}

@Composable
private fun EmbeddedQuoteTombstone(reason: UnavailableReason) {
    Text(
        text = stringResource(if (reason == UnavailableReason.DELETED) R.string.post_deleted else R.string.post_moderated),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(Spacing.m),
    )
}

@Composable
private fun PostActionsRow(
    likeCount: Int,
    commentCount: Int,
    repostCount: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onRepostClick: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.l)) {
        PostActionStat(Icons.Filled.FavoriteBorder, likeCount, stringResource(R.string.post_action_like), onLikeClick)
        PostActionStat(Icons.Filled.ModeComment, commentCount, stringResource(R.string.post_action_comment), onCommentClick)
        PostActionStat(Icons.Filled.Repeat, repostCount, stringResource(R.string.post_action_repost), onRepostClick)
    }
}

@Composable
private fun PostActionStat(icon: ImageVector, count: Int, contentDescription: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(20.dp)) {
            Icon(icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        }
        Text(count.toString(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun formatPlayCount(count: Int): String = when {
    count >= 1_000_000 -> "%.1fm".format(count / 1_000_000f)
    count >= 1_000 -> "%.1fk".format(count / 1_000f)
    else -> count.toString()
}
