package com.lf.fashion.data.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.postFilterDataStore: DataStore<Preferences> by preferencesDataStore(name = "post_filter_data_store")

class PostFilterDataStorePref @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        private val FILTER_GENDER = stringPreferencesKey("POST_FILTER_GENDER")
        private val FILTER_HEIGHT = stringPreferencesKey("POST_FILTER_HEIGHT")
        private val FILTER_WEIGHT = stringPreferencesKey("POST_FILTER_WEIGHT")
       // private val FILTER_BODY_TYPE = stringPreferencesKey("FILTER_BODY_TYPE")

        private val FILTER_TPO = stringPreferencesKey("POST_FILTER_TPO")
        private val FILTER_STYLE = stringPreferencesKey("POST_FILTER_STYLE")
        private val FILTER_SEASON = stringPreferencesKey("POST_FILTER_SEASON")
    }

    private val appContext = context.applicationContext
    val postGender: Flow<String?>
        get() = appContext.postFilterDataStore.data.map { preferences ->
            preferences[FILTER_GENDER]
        }

    val height: Flow<String?>
        get() = appContext.postFilterDataStore.data.map { preferences ->
            preferences[FILTER_HEIGHT]
        }
    val weight: Flow<String?>
        get() = appContext.postFilterDataStore.data.map { preferences ->
            preferences[FILTER_WEIGHT]
        }
/*    val bodyType: Flow<String?>
        get() = appContext.postFilterDataStore.data.map { preferences ->
            preferences[FILTER_BODY_TYPE]
        }*/
    val tpo: Flow<String?>
        get() = appContext.postFilterDataStore.data.map { preferences ->
            preferences[FILTER_TPO]
        }
    val style: Flow<String?>
        get() = appContext.postFilterDataStore.data.map { preferences ->
            preferences[FILTER_STYLE]
        }
    val season: Flow<String?>
        get() = appContext.postFilterDataStore.data.map { preferences ->
            preferences[FILTER_SEASON]
        }


    //main filter
    suspend fun saveMainFilterInstance(
        gender: String? = null,
        height: String? = null,
        weight: String? = null,
      //  bodyType: String? = null,
        tpo: String? = null,
        season: String? = null,
        style: String? = null
    ) {
        appContext.postFilterDataStore.edit { preferences ->
            gender?.let { preferences[FILTER_GENDER] = it }
            height?.let { preferences[FILTER_HEIGHT] = it }
            weight?.let { preferences[FILTER_WEIGHT] = it }
          //  bodyType?.let { preferences[FILTER_BODY_TYPE] = it }
            tpo?.let { preferences[FILTER_TPO] = it }
            season?.let { preferences[FILTER_SEASON] = it }
            style?.let { preferences[FILTER_STYLE] = it }
        }
    }


    suspend fun clearMainFilter(){
        appContext.postFilterDataStore.edit{ preferences->
            preferences.apply {
                remove(FILTER_GENDER)
                remove(FILTER_HEIGHT)
                remove(FILTER_WEIGHT)
               // remove(FILTER_BODY_TYPE)
                remove(FILTER_TPO)
                remove(FILTER_SEASON)
                remove(FILTER_STYLE)
            }
        }
    }

}