package com.example.streamingserviceregioncomparisonapp.ui.settings

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.streamingserviceregioncomparisonapp.data.DataStoreException
import com.example.streamingserviceregioncomparisonapp.data.RegionPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val regionPrefsMan = RegionPreferenceManager(application)
    val _countryIsValid = MutableStateFlow(true)
    val countryIsValid = _countryIsValid.asStateFlow()
    val regionName: LiveData<String> = regionPrefsMan.regionNameFlow
        .map {it ?: "United Kingdom"}
        .asLiveData()


    fun saveRegionName(newName: String) {
        viewModelScope.launch {
            try{
                regionPrefsMan.saveRegionName(newName)
                _countryIsValid.update { true }
            } catch (e: DataStoreException) {
                _countryIsValid.update { false }
                Log.e("DataStoreException", e.toString())
            }

        }
    }
}