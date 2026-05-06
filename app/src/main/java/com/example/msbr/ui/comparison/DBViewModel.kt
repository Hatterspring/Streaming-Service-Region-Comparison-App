package com.example.msbr.ui.comparison

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Room
import com.example.msbr.BuildConfig
import com.example.msbr.CompApp
import com.example.msbr.data.database.CompDatabase
import com.example.msbr.data.database.CompDatabase.Companion.DB_NAME
import com.example.msbr.data.database.MovieDetailsEntry
import com.example.msbr.data.database.ProviderEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class DBViewModel(application: Application): AndroidViewModel(application) {
    private val context: Context
        get() = getApplication()

    private val db = Room.databaseBuilder(context, CompDatabase::class.java, DB_NAME).build()


    /****************************************************
     PROVIDERS
     ****************************************************/
    fun getAllProviders(){

        viewModelScope.launch {
            Log.i("records",db.providerDao().getAll().toString())
        }

    }

    /****************************************************
     CACHE
     ****************************************************/
    suspend fun getMovieCache(): List<MovieDetailsEntry> {
        return db.movieDetailsDao().getAll()
    }
}

class DBViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val app =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CompApp
        return DBViewModel(app) as T
    }
}