package com.example.streamingserviceregioncomparisonapp.ui;



import androidx.lifecycle.ViewModel;
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MenuViewModel : ViewModel() {
    private val _uiState = MutableStateFlow("")
    val uiState = _uiState.asStateFlow()

    fun updateMovie(movie: String){
        _uiState.update({ movie })
    }
}
