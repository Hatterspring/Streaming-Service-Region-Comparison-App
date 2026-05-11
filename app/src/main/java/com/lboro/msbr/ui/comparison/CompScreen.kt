package com.lboro.msbr.ui.comparison

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lboro.msbr.data.codeToCountry
import com.lboro.msbr.data.countryToCode
import com.lboro.msbr.ui.settings.SettingsViewModel
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.lboro.msbr.data.database.MovieDetailsEntry
import com.lboro.msbr.gemini.GeminiImpl
import com.lboro.msbr.ui.Screens
import kotlinx.coroutines.launch
import kotlin.collections.containsKey
import kotlin.collections.get


@Composable
fun CompScreen(
    compViewModel: CompViewModel,
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    dbViewModel: DBViewModel,
    modifier: Modifier
) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //establish the context
    val context = LocalContext.current

    //collect state
    val movieState by compViewModel.movieState.collectAsState()
    val serviceState by compViewModel.streamingState.collectAsState()
    val typeState by compViewModel.typeState.collectAsState()
    val cachedState by compViewModel.cachedState.collectAsState()

    //establish state for movie details and service details
    val serviceListState = rememberLazyListState(0)
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var aiResponse by remember {mutableStateOf("loading AI response...")}

    //break down the mapping of region to service
    val keys = serviceState.keys.toList()
    val vals = serviceState.values.toList()

    //retrieve the region from local storage
    val region by settingsViewModel.regionName.observeAsState()

    /****************************************************
    FUNCTIONS
     ****************************************************/


    /*
     Is Available
     Inputs:
     * region: String
     * service: map of region to service by region
     * type: type of service
     Outputs:
     * Boolean
     Process:
     * get the country code of the region and check if
       the corresponding type of service has any matches.
     */
    fun isAvailable(): Boolean {
        val cc = region!!
        return when (typeState){
            CompViewModel.ServiceTypes.BUY -> serviceState.containsKey(cc) && !(serviceState[cc]?.buy?.isEmpty() ?: true)
            CompViewModel.ServiceTypes.RENT -> serviceState.containsKey(cc) && !(serviceState[cc]?.rent?.isEmpty() ?: true)
            CompViewModel.ServiceTypes.STREAM -> serviceState.containsKey(cc) && !(serviceState[cc]?.stream?.isEmpty() ?: true)
        }
    }

    fun availableServices(): String {
        val cc = region!!
        var out = ""
        var i = 0
        if (serviceState.containsKey(cc) && !(serviceState[cc]?.buy?.isEmpty() ?: true)) {out += "buy,"}
        if (serviceState.containsKey(cc) && !(serviceState[cc]?.rent?.isEmpty() ?: true)) {out += "rent,"}
        if (serviceState.containsKey(cc) && !(serviceState[cc]?.stream?.isEmpty() ?: true)) {out += "stream"}
        return out
    }

    /*
         Available Message
         Inputs:
         * region: String
         * service: map of region to service by region
         * type: type of service
         Outputs:
         * String
         Process:
         * if the region exists and the service is available,
           return an affirmative message, otherwise return a
           negative message
         */
    fun availableMessage(movie:String): String {
        val t = when (typeState) {
            CompViewModel.ServiceTypes.BUY -> "buy"
            CompViewModel.ServiceTypes.RENT -> "rent"
            CompViewModel.ServiceTypes.STREAM -> "stream"
        }
        if ((region != null) && isAvailable()) {
            return "$movie is available to $t in your region!"
        } else {
            return "$movie is not available to $t in your region."
        }
    }

    fun saveSearch(): MovieDetailsEntry {
        return MovieDetailsEntry(
            movie_id = movieState[0],
            name = movieState[1],
            description = movieState[2],
            image = movieState[3],
            release_date = movieState[4],
            rating = movieState[5],
            service_info = compViewModel.serviceDetailsToString()
        )
    }

    /****************************************************
     STRUCTURE
     ****************************************************/

    @Composable
    fun cacheButton() {
        when (cachedState) {
            false ->{
                Button(onClick = {
                    scope.launch{
                        dbViewModel.cacheMovie(saveSearch())
                        Log.i("cache contents", dbViewModel.getMovieCache().toString())
                    }
                }){
                    Text("Save this search")
                }
            }
            true -> {
                Button(onClick = {
                    scope.launch{
                        dbViewModel.clearCacheOf(movieState[1])
                        Log.i("cache contents", dbViewModel.getMovieCache().toString())
                    }
                    navController.popBackStack()
                }){
                    Text("Remove from Cache")
                }
            }
        }

    }
    @Composable
    fun compScreenPart1() {
        Column(){
            Text(
                text = try {
                    """
                    ${movieState[1]}
                    ${movieState[2]}
                    release date: ${movieState[4]}
                    rating: ${movieState[5]}
                    ${availableMessage(movieState[1])}
                """.trimIndent()
                } catch (e: ArrayIndexOutOfBoundsException) {
                    "Movie not found... Please wait or try a different search"
                }
            )
            cacheButton()
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick={
                        compViewModel.changeServiceType(CompViewModel.ServiceTypes.BUY)
                        scope.launch {
                            //dbViewModel.updateLinks()
                            serviceListState.animateScrollToItem(0)
                        }
                    }
                ) { Text("buy") }
                Button(
                    onClick={
                        compViewModel.changeServiceType(CompViewModel.ServiceTypes.RENT)
                        //compViewModel.scrollToTop(0,serviceListState)
                        scope.launch {
                            serviceListState.animateScrollToItem(0)
                        }
                    }
                ) { Text("rent") }
                Button(
                    onClick={
                        compViewModel.changeServiceType(CompViewModel.ServiceTypes.STREAM)
                        scope.launch {
                            serviceListState.animateScrollToItem(0)
                        }
                    }
                ) { Text("stream") }
            }
        }
    }


    @Composable
    fun serviceInformation(item: String) {
        Text(
            text = item,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    var url: String? = null
                    /*scope.launch {
                        url = dbViewModel.getLink(item)
                        dbViewModel.seedProviders()
                        dbViewModel.getAllProviders()
                        if (url != null) {
                            Log.i("url", url!!)
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                url!!.toUri()
                            )
                            context.startActivity(webIntent)
                        } else {
                            Toast.makeText(context, "Could not find link for service!", Toast.LENGTH_SHORT).show()
                        }
                    }*/

                }
        )
    }

    @Composable
    fun compScreenPart2() {
        LazyColumn (
            state = serviceListState,
            userScrollEnabled = true,
            modifier = Modifier
                .fillMaxHeight()
        ){
            itemsIndexed(vals) { index, listContent ->
                var expanded by remember { mutableStateOf(false) }
                var isButtonVisible by remember {mutableStateOf(false)}
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                    //.padding(8.dp)
                ) {
                    when (typeState) {
                        CompViewModel.ServiceTypes.BUY -> isButtonVisible = listContent.buy?.isEmpty() == false
                        CompViewModel.ServiceTypes.RENT -> isButtonVisible = listContent.rent?.isEmpty() == false
                        CompViewModel.ServiceTypes.STREAM -> isButtonVisible = listContent.stream?.isEmpty() == false
                    }
                    AnimatedVisibility(isButtonVisible) {
                        Button(
                            onClick = { expanded = !expanded },
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(keys[index])
                        }
                    }
                    AnimatedVisibility(expanded) {
                        Column() {
                            when(typeState) {
                                CompViewModel.ServiceTypes.BUY -> {
                                    listContent.buy?.forEach { item ->
                                        serviceInformation(item)
                                    }
                                }
                                CompViewModel.ServiceTypes.RENT -> {
                                    listContent.rent?.forEach { item ->
                                        serviceInformation(item)
                                    }
                                }
                                CompViewModel.ServiceTypes.STREAM -> {
                                    listContent.stream?.forEach { item ->
                                        serviceInformation(item)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (serviceState.isEmpty()) {
            Text("No streaming services could be found for this movie.")
        }
    }

    @Composable
    fun aiDialog() {
        Dialog(
            onDismissRequest = {showDialog = false},

        ) {
            Card{Column (
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        state= rememberScrollState()
                    )
            ) {
                Text(
                    text= aiResponse
                )
                Row() {
                    Button(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, aiResponse)
                            type="text/plain"
                        }

                        val shareIntent: Intent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Text("share")
                    }
                    Button(
                        onClick={
                            showDialog=false
                        }
                    ) {
                        Text("Close")
                    }
                }
            }}
        }
    }
    /*
    movieState[0] = movie ID
    movieState[1] = movie name
    movieState[2] = movie description
    movieState[3] = movie poster url
    movieState[4] = movie release date
    movieState[5] = movie rating
    movieState[6] = Enstringified ServiceByRegion (cache only)
     */
    BoxWithConstraints (){
        if (showDialog){
            aiDialog()
        }
        if (maxWidth < maxHeight) {
            Column(
                modifier = Modifier.fillMaxSize()
            ){
                compScreenPart1()
                compScreenPart2()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    compScreenPart1()
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    compScreenPart2()
                }
            }
        }
        AnimatedVisibility(
            !serviceState.isEmpty(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x=(-10).dp,y=(-10).dp)
        ) {
            FloatingActionButton(
                onClick={
                    showDialog = true
                    aiResponse = "loading AI response..."
                    scope.launch{aiResponse = GeminiImpl().summariseMovieData(compViewModel.serviceDetailsToString(),movieState[1],region!!) ?: "could not connect to Gemini..." }
                }
            ) { Icon(Icons.Filled.Star, "Share Floating Action Button") }
        }
    }
}







