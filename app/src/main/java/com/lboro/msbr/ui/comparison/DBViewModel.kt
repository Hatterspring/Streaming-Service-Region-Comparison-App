package com.lboro.msbr.ui.comparison

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Room
import com.lboro.msbr.CompApp
import com.lboro.msbr.data.database.CompDatabase
import com.lboro.msbr.data.database.CompDatabase.Companion.DB_NAME
import com.lboro.msbr.data.database.MovieDetailsEntry
import kotlinx.coroutines.launch

class DBViewModel(application: Application): AndroidViewModel(application) {
    private val context: Context
        get() = getApplication()

    private val db = Room.databaseBuilder(context, CompDatabase::class.java, DB_NAME)
        .fallbackToDestructiveMigration(false)
        //.addMigrations()
        .build()


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
    suspend fun cacheMovie(movieDetailsEntry: MovieDetailsEntry) {
        db.movieDetailsDao().insert(movieDetailsEntry)
    }
    suspend fun getMovieCache(): List<MovieDetailsEntry> {
        return db.movieDetailsDao().getAll()
    }

    suspend fun getMovieNames(): List<String> {
        return db.movieDetailsDao().getMovieNames()
    }

    suspend fun getMovie(movie: String): Array<MovieDetailsEntry>? {
        return db.movieDetailsDao().getMovie(movie)
    }

    suspend fun clearCache() {
        db.movieDetailsDao().clearCache()
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