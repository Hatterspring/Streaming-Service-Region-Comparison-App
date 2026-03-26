package com.example.streamingserviceregioncomparisonapp.ui

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState

@Composable
fun CompScreen(
    navController: NavController,
    compViewModel: CompViewModel,
    modifier: Modifier
) {
    Log.d("observed state", compViewModel.uiState.collectAsState().value)
    compViewModel.fetchMovieDetails("batman")
    Text(compViewModel.uiState.collectAsState().value)
}