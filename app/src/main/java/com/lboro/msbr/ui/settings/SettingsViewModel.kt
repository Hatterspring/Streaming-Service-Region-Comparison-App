package com.lboro.msbr.ui.settings

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.lboro.msbr.data.DataStoreException
import com.lboro.msbr.data.RegionPreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //store permissions granted boolean in state
    private val _permState: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    //obtain region name from local storage
    private val regionPrefsMan = RegionPreferenceManager(application)
    val regionName: LiveData<String> = regionPrefsMan.regionNameFlow
        .map {it}
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
     * save the region name if valid
     * fail if not
     * update the state to reflect whether the country
       was valid or not
     * return a boolean based on success
     */
    suspend fun saveRegionName(newName: String):Boolean {
        try{
            regionPrefsMan.saveRegionName(newName)
            return true
        } catch (e: DataStoreException) {
            Log.e("DataStoreException", e.toString())
            return false
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
}