package com.lboro.msbr.ui.menu

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel : ViewModel() {
    /****************************************************
     VARIABLES
     ****************************************************/

    //movieState: keep track of the movie name
    private val _movieState = MutableStateFlow("")
    val movieState = _movieState.asStateFlow()

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
}