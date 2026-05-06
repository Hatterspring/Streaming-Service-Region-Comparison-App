package com.example.msbr.ui.settings

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.msbr.data.DataStoreException
import com.example.msbr.data.RegionPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //store country is valid boolean in state
    val _countryIsValid = MutableStateFlow(true)
    val countryIsValid = _countryIsValid.asStateFlow()

    //store permissions granted boolean in state
    private val _permState: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    //obtain region name from local storage
    private val regionPrefsMan = RegionPreferenceManager(application)
    val regionName: LiveData<String> = regionPrefsMan.regionNameFlow
        .map {it ?: "United Kingdom"}
        .asLiveData()

    /****************************************************
     FUNCTIONS
     ****************************************************/
    /*
     Save Region Name
     Inputs:
     * newName: String
     Outputs:
     * none
     Process:
     * launch a coroutine
     * save the region name if valid
     * fail if not
     * update the state to reflect whether the country
       was valid or not
     */
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

    /*
     On Permission Change
     Inputs:
     * permission: String
     * isGranted: Boolean
     Outputs:
     * none
     Process:
     * update the ACCESS_COARSE_LOCATION permission
       to reflect whether it is granted or not
     */
    fun onPermissionChange(permission: String, isGranted: Boolean) {
        when (permission){
            ACCESS_COARSE_LOCATION -> _permState.update { isGranted }
            else -> Log.e("No recognised permission", "Permission not found: $permission")
        }
    }

    fun detectRegion() {

    }
}