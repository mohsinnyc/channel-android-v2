package com.channel.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.channel.R
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
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text(stringResource(R.string.action_retry))
        }
    }
}
