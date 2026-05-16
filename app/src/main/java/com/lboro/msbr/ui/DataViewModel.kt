package com.lboro.msbr.ui

import android.Manifest
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Room
import com.lboro.msbr.CompApp
import com.lboro.msbr.data.DataStoreException
import com.lboro.msbr.data.RegionPreferenceManager
import com.lboro.msbr.data.database.CompDatabase
import com.lboro.msbr.data.database.CompDatabase.Companion.DB_NAME
import com.lboro.msbr.data.database.MovieDetailsEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DataViewModel(application: Application) : AndroidViewModel(application) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //store permissions granted boolean in state
    private val _permState: MutableStateFlow<Boolean?> = MutableStateFlow(null)

    private val _dataCacheState: MutableStateFlow<List<MovieDetailsEntry>?> = MutableStateFlow(null)
    val dataCacheState = _dataCacheState.asStateFlow()
    private val _cacheNamesState: MutableStateFlow<List<String>> = MutableStateFlow(listOf<String>())
    val cacheNamesState = _cacheNamesState.asStateFlow()

    //obtain region name from local storage
    private val regionPrefsMan = RegionPreferenceManager(application)
    val regionName: LiveData<String> = regionPrefsMan.regionNameFlow
        .map {it}
        .asLiveData()

    private val context: Context
        get() = getApplication()

    private val db = Room.databaseBuilder(context, CompDatabase::class.java, DB_NAME)
        .fallbackToDestructiveMigration(false)
        //.addMigrations() /*for potential future live updates*/
        .build()


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
            Manifest.permission.ACCESS_COARSE_LOCATION -> _permState.update { isGranted }
            else -> Log.e("No recognised permission", "Permission not found: $permission")
        }
    }

    /****************************************************
    CACHE
     ****************************************************/
    suspend fun cacheMovie(movieDetailsEntry: MovieDetailsEntry) {
        db.movieDetailsDao().insert(movieDetailsEntry)
    }
    suspend fun getMovieCache(): List<MovieDetailsEntry>? {
        return db.movieDetailsDao().getAll()
    }

    fun getMovieNames() {
        viewModelScope.launch {
            _cacheNamesState.update { db.movieDetailsDao().getMovieNames() }
        }
    }

    suspend fun fetchMovie(movie: String): List<MovieDetailsEntry> {
            val result = db.movieDetailsDao().getMovie(movie)
            //_dataCacheState.update {result}
        return result
    }


    fun clearCache() {
        viewModelScope.launch {
            db.movieDetailsDao().clearCache()
        }
    }

    suspend fun clearCacheOf(movie: String) {
        _cacheNamesState.update { emptyList() }
        _dataCacheState.update { emptyList() }
        db.movieDetailsDao().delete(movie)
    }
}

class DataViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val app =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CompApp
        return DataViewModel(app) as T
    }
}