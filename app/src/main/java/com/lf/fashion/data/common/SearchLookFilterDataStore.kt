package com.lf.fashion.data.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lf.fashion.data.model.FilterItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.searchLookFilterDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_look_filter_data_store")

class SearchLookFilterDataStore @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        private val LOOK_FILTER_GENDER = stringPreferencesKey("LOOK_FILTER_GENDER")
        private val LOOK_FILTER_HEIGHT = intPreferencesKey("LOOK_FILTER_HEIGHT")
        private val LOOK_FILTER_WEIGHT = intPreferencesKey("LOOK_FILTER_WEIGHT")
        // private val FILTER_BODY_TYPE = stringPreferencesKey("FILTER_BODY_TYPE")

        private val LOOK_FILTER_TPO = stringPreferencesKey("LOOK_FILTER_TPO")
        private val LOOK_FILTER_STYLE = stringPreferencesKey("LOOK_FILTER_STYLE")
        private val LOOK_FILTER_SEASON = stringPreferencesKey("LOOK_FILTER_SEASON")
        private val LOOK_FILTER_TPO_ID = stringPreferencesKey("LOOK_FILTER_TPO_ID")
        private val LOOK_FILTER_STYLE_ID = stringPreferencesKey("LOOK_FILTER_STYLE_ID")
        private val LOOK_FILTER_SEASON_ID = stringPreferencesKey("LOOK_FILTER_SEASON_ID")
        //private val FILTER_PRICE = stringPreferencesKey("FILTER_PRICE")
        //private val FILTER_COLOR = stringPreferencesKey("FILTER_COLOR")
    }

    private val appContext = context.applicationContext

    val lookGender: Flow<String?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_GENDER]
        }
    val height: Flow<Int?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_HEIGHT]
        }
    val weight: Flow<Int?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_WEIGHT]
        }

    /*   val bodyType: Flow<String?>
           get() = appContext.searchLookFilterDataStore.data.map { preferences ->
               preferences[LOOK_FILTER_BODY_TYPE]
           }*/
    val tpo: Flow<String?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_TPO]
        }
    val style: Flow<String?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_STYLE]
        }
    val season: Flow<String?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_SEASON]
        }
    val tpoId: Flow<String?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_TPO_ID]
        }
    val styleId: Flow<String?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_STYLE_ID]
        }
    val seasonId: Flow<String?>
        get() = appContext.searchLookFilterDataStore.data.map { preferences ->
            preferences[LOOK_FILTER_SEASON_ID]
        }


    //        bodyType: String? = null,
    suspend fun saveLookFilterInstance(
        gender: String? = null,
        height: Int? = null,
        weight: Int? = null,
        tpo: FilterItem? = null,
        season: FilterItem? = null,
        style: FilterItem? = null
    ) {
        appContext.searchLookFilterDataStore.edit { preferences ->
            gender?.let { preferences[LOOK_FILTER_GENDER] = it }
            height?.let { preferences[LOOK_FILTER_HEIGHT] = it }
            weight?.let { preferences[LOOK_FILTER_WEIGHT] = it }
            //  bodyType?.let { preferences[LOOK_FILTER_BODY_TYPE] = it }
            tpo?.let {
                preferences[LOOK_FILTER_TPO] = it.text
                preferences[LOOK_FILTER_TPO_ID] = it.id
            }
            season?.let {
                preferences[LOOK_FILTER_SEASON] = it.text
                preferences[LOOK_FILTER_SEASON_ID] = it.id
            }
            style?.let {
                preferences[LOOK_FILTER_STYLE] = it.text
                preferences[LOOK_FILTER_STYLE_ID] = it.id
            }
        }
    }

    suspend fun clearLookFilter() {
        appContext.searchLookFilterDataStore.edit { preferences ->
            preferences.apply {
                remove(LOOK_FILTER_GENDER)
                remove(LOOK_FILTER_HEIGHT)
                remove(LOOK_FILTER_WEIGHT)
                //remove(LOOK_FILTER_BODY_TYPE)
                remove(LOOK_FILTER_TPO)
                remove(LOOK_FILTER_SEASON)
                remove(LOOK_FILTER_STYLE)
                remove(LOOK_FILTER_TPO_ID)
                remove(LOOK_FILTER_STYLE_ID)
                remove(LOOK_FILTER_SEASON_ID)
            }
        }
    }
}