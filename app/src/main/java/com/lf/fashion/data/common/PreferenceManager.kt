package com.lf.fashion.data.common

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
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

    val refreshToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }
    val searchHistoryList : Flow<String?>
    get() = appContext.dataStore.data.map { preferences ->
        preferences[RECENT_SEARCH_TERM]
    }
    suspend fun saveAccessTokens(accessToken: String, refreshToken: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken

        }
    }

    suspend fun storeSearchHistoryList(searchHistoryList : MutableList<String>){
        val serializeList = Gson().toJson(searchHistoryList)
        appContext.dataStore.edit { pref ->
            pref[RECENT_SEARCH_TERM] = serializeList
        }
    }


    suspend fun clear() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
        private val RECENT_SEARCH_TERM = stringPreferencesKey("recent_search_term")
    }
}
