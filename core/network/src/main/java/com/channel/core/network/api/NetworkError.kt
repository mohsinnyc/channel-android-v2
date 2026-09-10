package com.channel.core.network.api

/**
 * What went wrong, not how to say it — string resolution belongs in the UI
 * layer (via stringResource), never here. Keeping this module framework-free
 * also matters if/when a Kotlin Multiplatform target is added.
 */
sealed interface NetworkError {
    data class Server(val code: Int) : NetworkError
    data object NoConnection : NetworkError
    data object Unknown : NetworkError
}
