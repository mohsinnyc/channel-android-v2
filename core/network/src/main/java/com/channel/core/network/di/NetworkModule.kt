package com.channel.core.network.di

import com.channel.core.network.config.NetworkConfig
import com.channel.core.network.session.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.url
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Ktor's bearer Auth provider caches whatever loadTokens{} returns (even null) the
    // very first time ANY request goes through the client, and never calls it again
    // until a 401 triggers its internal refreshTokens flow. These endpoints run before
    // a token exists, so they must be excluded from preemptive auth — otherwise the
    // first signup/login call permanently poisons the cache with a null token and every
    // subsequent authenticated call (e.g. auth/status) goes out with no Authorization header.
    private val UNAUTHENTICATED_PATHS = setOf(
        "auth/signup",
        "auth/login",
        "auth/forgot-password",
        "auth/reset-password",
    )

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideExternalScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    @Singleton
    fun provideHttpClient(json: Json, tokenManager: TokenManager): HttpClient {
        return HttpClient(OkHttp) {
            expectSuccess = false

            install(ContentNegotiation) { json(json) }

            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
            }

            install(Auth) {
                bearer {
                    loadTokens {
                        val access = tokenManager.getAccessTokenOnce() ?: return@loadTokens null
                        val refresh = tokenManager.getRefreshTokenOnce().orEmpty()
                        BearerTokens(access, refresh)
                    }
                    // TODO: call POST /auth/refresh here once that's wired up client-side —
                    // returning null just means "give up on refresh," which still leaves
                    // login/signup itself working correctly today.
                    refreshTokens { null }
                    sendWithoutRequest { request ->
                        val path = request.url.encodedPath.trimStart('/')
                        path !in UNAUTHENTICATED_PATHS
                    }
                }
            }

            install(Logging) {
                level = LogLevel.INFO
            }

            defaultRequest {
                url(NetworkConfig.BASE_URL)
            }
        }
    }

    @Provides
    @Singleton
    @RawHttpClient
    fun provideRawHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            expectSuccess = false
            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
                connectTimeoutMillis = 10_000
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
}
