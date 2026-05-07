package com.example.msbr.ui.comparison

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.msbr.data.codeToCountry
import com.example.msbr.data.countryToCode
import com.example.msbr.ui.settings.SettingsViewModel
import androidx.core.net.toUri
import kotlinx.coroutines.launch
import kotlin.collections.containsKey
import kotlin.collections.get


@Composable
fun CompScreen(
    compViewModel: CompViewModel,
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

    //establish state for movie details and service details
    val movieListState = rememberLazyListState()
    val serviceListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    //break down the mapping of region to service
    val keys = serviceState.keys.toList()
    val vals = serviceState.values.toList()

    //retrieve the region from local storage
    val region = settingsViewModel.regionName.value

    /****************************************************
     STRUCTURE
     ****************************************************/
    /*
    movieState[0] = movie ID
    movieState[1] = movie name
    movieState[2] = movie description
    movieState[3] = movie poster url
    movieState[4] = movie release date
    movieState[5] = movie rating
     */
    Column {
        Text(
            text = try {
                """
                    ${movieState[1]}
                    ${movieState[2]}
                    release date: ${movieState[4]}
                    rating: ${movieState[5]}
                    ${(movieState[1]) + availableMessage(region, serviceState, typeState)}
                """.trimIndent()
            } catch (e: ArrayIndexOutOfBoundsException) {
                "Movie not found... Please wait or try a different search"
            }
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick={
                    compViewModel.changeServiceType(CompViewModel.ServiceTypes.BUY)
                    scope.launch {
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
                            Text(codeToCountry(keys[index]) ?: "")
                        }
                    }
                    AnimatedVisibility(expanded) {
                        Column() {
                            when(typeState) {
                                CompViewModel.ServiceTypes.BUY -> {
                                    listContent.buy?.forEach { item ->
                                        Text(
                                            text = item,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val webIntent = Intent(
                                                        Intent.ACTION_VIEW,
                                                        "https://www.netflix.com".toUri()
                                                    )
                                                    context.startActivity(webIntent)
                                                }
                                        )
                                    }
                                }
                                CompViewModel.ServiceTypes.RENT -> {
                                    listContent.rent?.forEach { item ->
                                        Text(
                                            text = item,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val webIntent = Intent(
                                                        Intent.ACTION_VIEW,
                                                        "https://www.netflix.com".toUri()
                                                    )
                                                    context.startActivity(webIntent)
                                                }
                                        )
                                    }
                                }
                                CompViewModel.ServiceTypes.STREAM -> {
                                    listContent.stream?.forEach { item ->
                                        Text(
                                            text = item,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val webIntent = Intent(
                                                        Intent.ACTION_VIEW,
                                                        "https://www.netflix.com".toUri()
                                                    )
                                                    context.startActivity(webIntent)
                                                }
                                        )
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
    FloatingActionButton(
        onClick={
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                //TODO: change the sharing text so that it displays service information
                putExtra(Intent.EXTRA_TEXT, "hello this is extra text")
                type="text/plain"
            }

            val shareIntent: Intent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        },
    ) { Icon(Icons.Filled.Share, "Share Floating Action Button") }
}

/****************************************************
 FUNCTIONS
 ****************************************************/
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
fun availableMessage(region: String?, service: Map<String, CompViewModel.ServiceByRegion>, type: CompViewModel.ServiceTypes): String {
    val t = when (type){
        CompViewModel.ServiceTypes.BUY -> "buy"
        CompViewModel.ServiceTypes.RENT -> "rent"
        CompViewModel.ServiceTypes.STREAM -> "stream"
    }
    if (!(region?.isEmpty() ?: true) && isAvailable(region, service, type)) {
        return " is available to $t in your region!"
    } else {
        return " is not available to $t in your region."
    }
}

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
fun isAvailable(region: String, service: Map<String, CompViewModel.ServiceByRegion>, type: CompViewModel.ServiceTypes): Boolean {
    val cc = countryToCode(region)
    return when (type){
        CompViewModel.ServiceTypes.BUY -> service.containsKey(cc) && !(service[cc]?.buy?.isEmpty() ?: true)
        CompViewModel.ServiceTypes.RENT -> service.containsKey(cc) && !(service[cc]?.rent?.isEmpty() ?: true)
        CompViewModel.ServiceTypes.STREAM -> service.containsKey(cc) && !(service[cc]?.stream?.isEmpty() ?: true)
    }
}

