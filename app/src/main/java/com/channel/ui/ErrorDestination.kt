package com.channel.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.channel.R
import com.channel.core.designsystem.components.PrimaryButton
import com.channel.core.designsystem.theme.Spacing
import com.channel.core.network.api.NetworkError

@Composable
fun ErrorDestination(reason: NetworkError, onRetry: () -> Unit) {
    val message = when (reason) {
        NetworkError.Unauthorized -> stringResource(R.string.error_unauthorized)
        is NetworkError.Server -> stringResource(R.string.error_server, reason.code)
        NetworkError.NoConnection -> stringResource(R.string.error_no_connection)
        NetworkError.Unknown -> stringResource(R.string.error_unknown)
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(Spacing.l))
        PrimaryButton(text = stringResource(R.string.action_retry), onClick = onRetry)
    }
}
