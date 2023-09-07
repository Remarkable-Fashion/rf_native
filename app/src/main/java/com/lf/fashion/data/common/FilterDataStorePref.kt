package com.lf.fashion.data.common

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kakao.sdk.auth.Constants.ACCESS_TOKEN
import com.lf.fashion.TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.filterDataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_data_store")

class FilterDataStorePref @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        private val FILTER_GENDER = stringPreferencesKey("FILTER_GENDER")
        private val FILTER_ITEM_GENDER = stringPreferencesKey("FILTER_ITEM_GENDER")

        private val FILTER_HEIGHT = stringPreferencesKey("FILTER_HEIGHT")
        private val FILTER_WEIGHT = stringPreferencesKey("FILTER_WEIGHT")
        private val FILTER_BODY_TYPE = stringPreferencesKey("FILTER_BODY_TYPE")

        private val FILTER_TPO = stringPreferencesKey("FILTER_TPO")
        private val FILTER_STYLE = stringPreferencesKey("FILTER_STYLE")
        private val FILTER_SEASON = stringPreferencesKey("FILTER_SEASON")

        private val FILTER_PRICE = stringPreferencesKey("FILTER_PRICE")
        private val FILTER_COLOR = stringPreferencesKey("FILTER_COLOR")
    }

    private val appContext = context.applicationContext
    val mainGender: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_GENDER]
        }
    val itemGender: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_GENDER]
        }
    val height: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_HEIGHT]
        }
    val weight: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_WEIGHT]
        }
    val bodyType: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_BODY_TYPE]
        }
    val tpo: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_TPO]
        }
    val style: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_STYLE]
        }
    val season: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_SEASON]
        }
    val price: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_PRICE]
        }
    val color: Flow<String?>
        get() = appContext.filterDataStore.data.map { preferences ->
            preferences[FILTER_COLOR]
        }

    //main filter
    suspend fun saveMainFilterInstance(
        gender: String? = null,
        height: String? = null,
        weight: String? = null,
        bodyType: String? = null,
        tpo: String? = null,
        season: String? = null,
        style: String? = null
    ) {
        appContext.filterDataStore.edit { preferences ->
            gender?.let { preferences[FILTER_GENDER] = it }
            height?.let { preferences[FILTER_HEIGHT] = it }
            weight?.let { preferences[FILTER_WEIGHT] = it }
            bodyType?.let { preferences[FILTER_BODY_TYPE] = it }
            tpo?.let { preferences[FILTER_TPO] = it }
            season?.let { preferences[FILTER_SEASON] = it }
            style?.let { preferences[FILTER_STYLE] = it }
        }
    }
    suspend fun saveItemFilterInstance(
        gender: String? = null,
        price: String? = null,
        color: String? = null
    ) {
        appContext.filterDataStore.edit { preferences ->
            gender?.let { preferences[FILTER_ITEM_GENDER] = it }
            price?.let { preferences[FILTER_PRICE] = it }
            color?.let { preferences[FILTER_COLOR] = it }
        }
    }

    suspend fun clearMainFilter(){
        appContext.filterDataStore.edit{preferences->
            preferences.apply {
                remove(FILTER_GENDER)
                remove(FILTER_HEIGHT)
                remove(FILTER_WEIGHT)
                remove(FILTER_BODY_TYPE)
                remove(FILTER_TPO)
                remove(FILTER_SEASON)
                remove(FILTER_STYLE)
            }
        }
    }
    suspend fun clearItemFilter(){
        appContext.filterDataStore.edit{preferences->
            preferences.apply {
                remove(FILTER_ITEM_GENDER)
                remove(FILTER_PRICE)
                remove(FILTER_COLOR)
            }
        }
    }
}