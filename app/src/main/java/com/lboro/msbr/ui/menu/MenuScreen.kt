package com.lboro.msbr.ui.menu

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lboro.msbr.ui.DataViewModel
import com.lboro.msbr.ui.Screens
import com.lboro.msbr.ui.comparison.CompViewModel
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    navController: NavController,
    viewmodel: MenuViewModel,
    compViewModel: CompViewModel,
    dataViewModel: DataViewModel,
    modifier: Modifier
) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //establish context and movie name state
    var movie by remember {mutableStateOf("")}
    val context = LocalContext.current

    val movieNames by dataViewModel.cacheNamesState.collectAsState()

    val scope = rememberCoroutineScope()

    dataViewModel.getMovieNames()
    try {
        viewmodel.getMovieCache(movieNames)
    } catch (e: NullPointerException) {
        Log.e("No cache entries", e.toString())
    }


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
            text="Find where your movie is...",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        TextField(
            label =  {Text("Enter your movie here")},
            value = movie,
            onValueChange = {
                //collect the name of the movie to search for
                movie = it
                viewmodel.updateMovie(movie)
            },
            shape = RoundedCornerShape(50.dp),
            singleLine = true,
            colors= TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp)


        )
        Button(
            onClick = {
                //navigate to comparison screen
                compViewModel.fetchMovieDetails(movie, dataViewModel.regionName.value ?: "United Kingdom", context)
                navController.navigate(Screens.Comp.name)
            },
            modifier = Modifier.padding(all=10.dp)
        ) { Text("Find!") }
        if (savedMovies.value.size > 0) {
            Text("Saved Movies: ")
        } else {

            Text("Could not find any saved movies")
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
                        scope.launch {
                            try {
                                compViewModel.fetchCachedMovieDetails(dataViewModel.fetchMovie(movie), context)
                                navController.navigate(Screens.Comp.name)
                            } catch (e: NullPointerException) {
                                Log.e("No movie cache found", e.toString())
                            }
                        }
                    }
                )
            }
        }
        Button(
            onClick = {
                viewmodel.clearMovieCache()
                dataViewModel.clearCache()
            }
        ) {
            Text("Clear Cache")
        }
    }
}