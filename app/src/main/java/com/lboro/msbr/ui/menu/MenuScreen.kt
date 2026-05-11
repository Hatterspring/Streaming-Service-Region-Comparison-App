package com.lboro.msbr.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.lboro.msbr.ui.comparison.DBViewModel
import com.lboro.msbr.ui.settings.SettingsViewModel

@Composable
fun MenuScreen(
    navController: NavController,
    viewmodel: MenuViewModel,
    compViewModel: CompViewModel,
    settingsViewModel: SettingsViewModel,
    dbViewModel: DBViewModel,
    modifier: Modifier
) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //establish context and movie name state
    var movie by remember {mutableStateOf("")}
    val context = LocalContext.current

    viewmodel.getMovieCache(dbViewModel)
    var savedMovies = viewmodel.cachedMovieState.collectAsState()


    /****************************************************
     STRUCTURE
     ****************************************************/
    Column(
        modifier = Modifier
            .fillMaxSize(),
            //.offset(y=(-100).dp),
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
                compViewModel.fetchMovieDetails(movie, settingsViewModel.regionName.value ?: "United Kingdom", context)
                navController.navigate(Screens.Comp.name)
            },
            modifier = Modifier.padding(all=10.dp)
        ) { Text("Find!") }
        if (savedMovies.value.size > 0) {
            Text("Saved Movies: ")
        }
        LazyColumn(
            state = rememberLazyListState(),
            userScrollEnabled = true,
            modifier = Modifier.height(200.dp)
        ) {
            itemsIndexed(items=savedMovies.value) { index, movie ->
                Text(
                    text=movie,
                    modifier = Modifier.clickable() {
                        compViewModel.fetchCachedMovieDetails(movie, dbViewModel, context)
                        navController.navigate(Screens.Comp.name)
                    },
                )
            }
        }
    }
}