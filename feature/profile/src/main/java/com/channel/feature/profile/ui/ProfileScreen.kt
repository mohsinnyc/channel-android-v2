package com.channel.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.feature.profile.R
import com.channel.feature.profile.data.PostSummary
import com.channel.feature.profile.data.ProfileResponse
import com.channel.feature.profile.ui.components.message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onRetry: () -> Unit,
    onToggleVoiceBioPlayback: () -> Unit,
    onTogglePostPlayback: (PostSummary) -> Unit,
    onLogout: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.profile?.username ?: stringResource(R.string.profile_title_fallback)) },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.profile_settings_action))
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.profile_logout_action)) },
                            onClick = {
                                menuExpanded = false
                                onLogout()
                            },
                        )
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            uiState.loadError != null -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(uiState.loadError.message(), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(Spacing.l))
                    PrimaryButton(text = stringResource(R.string.profile_retry_action), onClick = onRetry)
                }
            }
            uiState.profile != null -> ProfileContent(
                profile = uiState.profile,
                isVoiceBioPlaying = uiState.playback.isPlaying && uiState.playback.mediaId == uiState.profile.audioBioUrl,
                playingPostId = uiState.playback.mediaId.takeIf { uiState.playback.isPlaying },
                onToggleVoiceBioPlayback = onToggleVoiceBioPlayback,
                onTogglePostPlayback = onTogglePostPlayback,
                contentPadding = padding,
            )
        }
    }
}

@Composable
private fun ProfileContent(
    profile: ProfileResponse,
    isVoiceBioPlaying: Boolean,
    playingPostId: String?,
    onToggleVoiceBioPlayback: () -> Unit,
    onTogglePostPlayback: (PostSummary) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + Spacing.l,
            bottom = contentPadding.calculateBottomPadding() + Spacing.l,
            start = Spacing.l,
            end = Spacing.l,
        ),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
                ProfileHeader(
                    profile = profile,
                    isVoiceBioPlaying = isVoiceBioPlaying,
                    onToggleVoiceBioPlayback = onToggleVoiceBioPlayback,
                )
                profile.bio?.let { bio ->
                    Text(bio, style = MaterialTheme.typography.bodyMedium)
                }
                StatsRow(profile)
                Text(stringResource(R.string.profile_posts_section_title), style = MaterialTheme.typography.titleMedium)
            }
        }
        items(profile.posts, key = { it.postId }) { post ->
            PostCard(
                post = post,
                isPlaying = playingPostId == post.audioUrl,
                onTogglePlayback = { onTogglePostPlayback(post) },
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: ProfileResponse,
    isVoiceBioPlaying: Boolean,
    onToggleVoiceBioPlayback: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.l)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Box {
                Avatar(imageUrl = profile.profileImageUrl, fallbackText = profile.username, size = 84.dp)
                if (profile.audioBioUrl.isNotBlank()) {
                    IconButton(
                        onClick = onToggleVoiceBioPlayback,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(34.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                    ) {
                        Icon(
                            imageVector = if (isVoiceBioPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                            contentDescription = stringResource(
                                if (isVoiceBioPlaying) R.string.profile_stop_voice_bio else R.string.profile_play_voice_bio
                            ),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
            if (profile.audioBioUrl.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    Icon(
                        Icons.Filled.Hearing,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        formatPlayCount(profile.audioBioPlayCount),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                Text(profile.username, style = MaterialTheme.typography.titleLarge)
                if (profile.isVerified) {
                    Icon(
                        Icons.Filled.Verified,
                        contentDescription = stringResource(R.string.profile_verified_badge),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp),
                    )
                }
                if (profile.isPrivate) {
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = stringResource(R.string.profile_private_badge),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsRow(profile: ProfileResponse) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatItem(value = profile.posts.size.toString(), label = stringResource(R.string.profile_stat_posts))
        StatItem(value = profile.listenerCount.toString(), label = stringResource(R.string.profile_stat_listeners))
        StatItem(value = profile.listeningCount.toString(), label = stringResource(R.string.profile_stat_listening))
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(value, style = MaterialTheme.typography.titleMedium)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun Avatar(imageUrl: String?, fallbackText: String, size: Dp) {
    if (imageUrl != null) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.size(size).clip(CircleShape),
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                fallbackText.take(2).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

private fun formatPlayCount(count: Int): String = when {
    count >= 1_000_000 -> "%.1fm".format(count / 1_000_000f)
    count >= 1_000 -> "%.1fk".format(count / 1_000f)
    else -> count.toString()
}
