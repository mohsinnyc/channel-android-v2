package com.channel.core.network.di

import javax.inject.Qualifier

/**
 * The plain OkHttpClient — no auth interceptor, no default base URL. Use this
 * for requests to third-party hosts (e.g. an S3 presigned URL): the primary
 * OkHttpClient would otherwise attach our backend's bearer token to every
 * request it makes, including to hosts that have no business seeing it.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RawHttpClient
