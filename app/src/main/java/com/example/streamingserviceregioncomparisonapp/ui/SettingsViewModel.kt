package com.example.streamingserviceregioncomparisonapp.ui;

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.streamingserviceregioncomparisonapp.data.RegionPreferenceManager
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val regionPrefsMan = RegionPreferenceManager(application)
    val regionName: LiveData<String> = regionPrefsMan.regionNameFlow
        .map {it ?: "regionName"}
        .asLiveData()

    fun saveRegionName(newName: String) {
        viewModelScope.launch {
            regionPrefsMan.saveRegionName(newName)
        }
    }
}
