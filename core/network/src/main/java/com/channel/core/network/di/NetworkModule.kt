package com.channel.core.network.di

import com.channel.core.network.config.NetworkConfig
import com.channel.core.network.session.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

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
        return HttpClient(Android) {
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
                    // TODO: wire this up once the login/refresh-token endpoints are built —
                    // returning null here just means "give up," which is correct today
                    // since nothing can be logged in yet.
                    refreshTokens { null }
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
}
