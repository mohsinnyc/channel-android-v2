package com.channel.core.network.api

fun ApiResult.Error.toNetworkError(): NetworkError =
    if (code == 401) NetworkError.Unauthorized else NetworkError.Server(code)

fun ApiResult.Exception.toNetworkError(): NetworkError = NetworkError.NoConnection
