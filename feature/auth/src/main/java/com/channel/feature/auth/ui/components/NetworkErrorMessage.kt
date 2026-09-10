package com.channel.feature.auth.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.channel.core.network.api.NetworkError
import com.channel.feature.auth.R

/**
 * The generic reading of Unauthorized: a session that expired mid-screen.
 * Login is the one place a 401 instead means "wrong credentials" — it
 * overrides this case locally rather than through this shared mapper.
 */
@Composable
fun NetworkError.message(): String = when (this) {
    NetworkError.Unauthorized -> stringResource(R.string.error_network_session_expired)
    is NetworkError.Server -> stringResource(R.string.error_network_server, code)
    NetworkError.NoConnection -> stringResource(R.string.error_network_no_connection)
    NetworkError.Unknown -> stringResource(R.string.error_network_unknown)
}
