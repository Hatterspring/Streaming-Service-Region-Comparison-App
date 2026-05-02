package com.example.streamingserviceregioncomparisonapp.ui.menu

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.streamingserviceregioncomparisonapp.ui.menu.MenuViewModel
import com.example.streamingserviceregioncomparisonapp.ui.Screens
import com.example.streamingserviceregioncomparisonapp.ui.comparison.CompViewModel
import com.example.streamingserviceregioncomparisonapp.ui.comparison.MovieNotFoundException

@Composable
fun MenuScreen(
    navController: NavController,
    viewmodel: MenuViewModel,
    compViewModel: CompViewModel,
    modifier: Modifier
) {
    var movie by remember {mutableStateOf("")}
    val context = LocalContext.current
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
            compViewModel.fetchMovieDetails(movie, context)
            navController.navigate(Screens.Comp.name)
        }) { Text("Find!") }

    }
}