package com.example.msbr.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "regionPreference")

class RegionPreferenceManager(private val context: Context) {
    //store the saved region name and code in local storage
    companion object{
        private val REGION_NAME_KEY = stringPreferencesKey("regionName")
        private val REGION_CODE_KEY = stringPreferencesKey("regionCode")
    }

    //make the region name accessible
    val regionNameFlow: Flow<String?> =context.dataStore.data
        .map {preferences ->
            preferences[REGION_NAME_KEY]
        }

    /*
     Save Region Name
     Inputs:
     * regionName: String
     Outputs:
     * none
     Process:
     * obtain the code of the input country
     * if the country does not exist, the code will be empty
     * if the code is empty, throw an exception
     * otherwise, store both the region name and code
     */
    @Throws(DataStoreException::class)
    suspend fun saveRegionName(regionName: String) {
        val cc: String = (countryToCode(regionName) ?: "")
        if (cc.isEmpty()) {
            throw DataStoreException("country does not exist!")
        }
        context.dataStore.edit { preferences ->
            preferences[REGION_NAME_KEY] = regionName
            preferences[REGION_CODE_KEY] = cc
        }
    }
}