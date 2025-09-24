package com.pingpong.app.core.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val TOKEN_KEY = stringPreferencesKey("auth_token")
private val ROLE_KEY = stringPreferencesKey("user_role")

@Singleton
class TokenManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val tokenFlow: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }
    val roleFlow: Flow<String?> = dataStore.data.map { it[ROLE_KEY] }

    suspend fun saveToken(token: String, role: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[ROLE_KEY] = role
        }
    }

    suspend fun clearToken() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
            prefs.remove(ROLE_KEY)
        }
    }
}
