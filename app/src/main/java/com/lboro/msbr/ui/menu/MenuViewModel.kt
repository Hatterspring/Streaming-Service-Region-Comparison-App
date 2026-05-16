package com.lboro.msbr.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lboro.msbr.data.database.MovieDetailsEntry
import com.lboro.msbr.ui.DataViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    /****************************************************
     VARIABLES
     ****************************************************/
    //movieState: keep track of the movie name
    private val _movieState = MutableStateFlow("")

    private val _cachedMovieState: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val cachedMovieState = _cachedMovieState.asStateFlow()

    /****************************************************
     FUNCTIONS
     ****************************************************/

    /*
     Update Movie
     Inputs:
     * movie: String
     Outputs:
     * none
     Process:
     * update the movie name in state
     */
    fun updateMovie(movie: String){
        _movieState.update({ movie })
    }

    fun getMovieCache(cache: List<String>) {
        viewModelScope.launch {
            //dbViewModel.clearCache()
            _cachedMovieState.update{cache}
        }
    }

    fun clearMovieCache() {
        viewModelScope.launch {
            _cachedMovieState.update { emptyList() }
        }
    }
}