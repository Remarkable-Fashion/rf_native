package com.lf.fashion.data.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.searchItemFilterDataStore: DataStore<Preferences> by preferencesDataStore(name = "search_item_filter_data_store")

class SearchItemFilterDataStore @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        private val ITEM_FILTER_GENDER = stringPreferencesKey("ITEM_FILTER_GENDER")
        private val ITEM_FILTER_MIN_PRICE = intPreferencesKey("ITEM_FILTER_MIN_PRICE")
        private val ITEM_FILTER_MAX_PRICE = intPreferencesKey("ITEM_FILTER_MAX_PRICE")
        private val ITEM_FILTER_COLOR = stringPreferencesKey("ITEM_FILTER_COLOR")


    }
    private val appContext = context.applicationContext
    val itemGender: Flow<String?>
        get() = appContext.searchItemFilterDataStore.data.map { preferences ->
            preferences[ITEM_FILTER_GENDER]
        }
    val minPrice: Flow<Int?>
        get() = appContext.searchItemFilterDataStore.data.map { preferences ->
            preferences[ITEM_FILTER_MIN_PRICE]
        }
    val maxPrice: Flow<Int?>
        get() = appContext.searchItemFilterDataStore.data.map { preferences ->
            preferences[ITEM_FILTER_MAX_PRICE]
        }
    val color: Flow<String?>
        get() = appContext.searchItemFilterDataStore.data.map { preferences ->
            preferences[ITEM_FILTER_COLOR]
        }

    suspend fun saveItemFilterInstance(
        gender: String? = null,
        minPrice: Int? = null,
        maxPrice :Int?=null,
        color: String? = null
    ) {
        appContext.searchItemFilterDataStore.edit { preferences ->
            gender?.let { preferences[ITEM_FILTER_GENDER] = it }
            minPrice?.let { preferences[ITEM_FILTER_MIN_PRICE] = it }
            maxPrice?.let{preferences[ITEM_FILTER_MAX_PRICE] = it}
            color?.let { preferences[ITEM_FILTER_COLOR] = it }
        }
    }

    suspend fun clearItemFilter(){
        appContext.searchItemFilterDataStore.edit{ preferences->
            preferences.apply {
                remove(ITEM_FILTER_GENDER)
                remove(ITEM_FILTER_MIN_PRICE)
                remove(ITEM_FILTER_MAX_PRICE)
                remove(ITEM_FILTER_COLOR)
            }
        }
    }
}