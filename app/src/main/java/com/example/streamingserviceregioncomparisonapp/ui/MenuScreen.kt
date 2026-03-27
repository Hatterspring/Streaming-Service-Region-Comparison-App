package com.example.streamingserviceregioncomparisonapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MenuScreen(
        navController: NavController,
        viewmodel: MenuViewModel,
        compViewModel: CompViewModel,
        modifier: Modifier
) {
    var movie by remember {mutableStateOf("")}
    Column {
        Text("Find where your movie is...")
        TextField(
            label =  {Text("Enter your movie here")},
            value = movie,
            onValueChange = {
                movie = it
                viewmodel.updateMovie(movie)
            }
        )
        Button(onClick = {
            compViewModel.fetchMovieDetails(movie)
            navController.navigate(Screens.Comp.name)
        }) { Text("Find!") }

    }
}