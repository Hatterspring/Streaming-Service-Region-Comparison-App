package com.lboro.msbr.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lboro.msbr.ui.Screens
import com.lboro.msbr.ui.comparison.CompViewModel

@Composable
fun MenuScreen(
    navController: NavController,
    viewmodel: MenuViewModel,
    compViewModel: CompViewModel,
    modifier: Modifier
) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //establish context and movie name state
    var movie by remember {mutableStateOf("")}
    val context = LocalContext.current

    /****************************************************
     STRUCTURE
     ****************************************************/
    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset(y=(-100).dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text="Find where your movie is..."
        )
        TextField(
            label =  {Text("Enter your movie here")},
            value = movie,
            onValueChange = {
                //collect the name of the movie to search for
                movie = it
                viewmodel.updateMovie(movie)
            },
            modifier = Modifier.padding(all=10.dp)
        )
        Button(
            onClick = {
            //navigate to comparison screen
            compViewModel.fetchMovieDetails(movie, context)
            navController.navigate(Screens.Comp.name)
            },
            modifier = Modifier.padding(all=10.dp)
        ) { Text("Find!") }
    }
}