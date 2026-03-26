package com.example.streamingserviceregioncomparisonapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "regionPreference")

class RegionPreferenceManager(private val context: Context) {
    companion object{
        private val REGION_NAME_KEY = stringPreferencesKey("regionName")
    }
    val regionNameFlow: Flow<String?> =context.dataStore.data
        .map {preferences ->
            preferences[REGION_NAME_KEY]
        }

    suspend fun saveRegionName(regionName: String) {
        context.dataStore.edit { preferences ->
            preferences[REGION_NAME_KEY] = regionName
        }
    }
}