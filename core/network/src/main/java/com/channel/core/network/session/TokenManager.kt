package com.channel.core.network.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore by preferencesDataStore(name = "auth_tokens")

/**
 * Access/refresh tokens live in DataStore, sandboxed to this app by the OS the
 * same way SharedPreferences was. Not additionally encrypted at rest: these are
 * short-lived, rotatable JWTs, not long-term secrets, and androidx.security.crypto
 * (the old encrypted-prefs path) is no longer actively maintained with no clear
 * successor yet — revisit if that changes.
 */
@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val accessToken: Flow<String?> = context.tokenDataStore.data.map { it[Keys.ACCESS_TOKEN] }

    suspend fun getAccessTokenOnce(): String? = accessToken.first()

    suspend fun getRefreshTokenOnce(): String? =
        context.tokenDataStore.data.map { it[Keys.REFRESH_TOKEN] }.first()

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.tokenDataStore.edit { prefs ->
            prefs[Keys.ACCESS_TOKEN] = accessToken
            prefs[Keys.REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clear() {
        context.tokenDataStore.edit { it.clear() }
    }
}
