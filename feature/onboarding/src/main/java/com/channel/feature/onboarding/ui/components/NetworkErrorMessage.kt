package com.channel.feature.onboarding.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.channel.core.network.api.NetworkError
import com.channel.feature.onboarding.R

@Composable
fun NetworkError.message(): String = when (this) {
    NetworkError.Unauthorized -> stringResource(R.string.error_network_session_expired)
    is NetworkError.Server -> stringResource(R.string.error_network_server, code)
    NetworkError.NoConnection -> stringResource(R.string.error_network_no_connection)
    NetworkError.Unknown -> stringResource(R.string.error_network_unknown)
}
