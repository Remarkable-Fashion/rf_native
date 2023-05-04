package com.lf.fashion.data.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name="my_data_store")

class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {
    private val appContext = context.applicationContext

    val accessToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    suspend fun saveAccessTokens(accessToken: String, refreshToken: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken

        }
    }

    val refreshToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }

    val loginUserId: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }

    suspend fun saveUserId(userId: String) {
        appContext.dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    suspend fun clear() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val USER_ID = stringPreferencesKey("login_user_id")
        private val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
    }
}
