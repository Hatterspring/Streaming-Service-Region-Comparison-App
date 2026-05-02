package com.example.streamingserviceregioncomparisonapp.ui.comparison

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.streamingserviceregioncomparisonapp.data.codeToCountry
import com.example.streamingserviceregioncomparisonapp.data.countryToCode
import com.example.streamingserviceregioncomparisonapp.ui.settings.SettingsViewModel


@Composable
fun CompScreen(
    navController: NavController,
    compViewModel: CompViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {
    val movieState by compViewModel.movieState.collectAsState()
    val streamingState by compViewModel.streamingState.collectAsState()
    val movieListState = rememberLazyListState()
    val streamingListState = rememberLazyListState()
    val keys = streamingState.keys.toList()
    val vals = streamingState.values.toList()
    val region = settingsViewModel.regionName.value
    Text(movieState[0])
    Column {
        LazyColumn (
            state = movieListState
        ){
            itemsIndexed(movieState) { index, listContent ->
                Text(listContent)
            }
        }
        Text(
            text = try {(movieState[1]) + availableMessage(region, streamingState)} catch (e: ArrayIndexOutOfBoundsException) {"Movie not found..."}
        )
        LazyColumn (
            state = streamingListState,
            userScrollEnabled = true
        ){
            itemsIndexed(vals) { index, listContent ->
                Text(
                    "Results for ${codeToCountry(keys[index])}:\n" +
                            "  Available to stream on ${listContent.fr?.toString()}\n" +
                            "  Available to buy on ${listContent.b?.toString()}\n" +
                            "  Available to rent on ${listContent.r?.toString()}\n" +
                            "-------------------------------------------------"

                )
            }
        }
        if (streamingState.isEmpty()) {
            Text("No streaming services could be found for this movie.")
        }
    }


}

fun availableMessage(region: String?, streamingState: Map<String, CompViewModel.SSByRegion>): String {
    if (!(region?.isEmpty() ?: true) && isAvailable(region, streamingState)) {
        return " is available to stream in your region!"
    } else {
        return " is not available to stream in your region."
    }
}

fun isAvailable(region: String, streamingState: Map<String, CompViewModel.SSByRegion>): Boolean {
    val cc = countryToCode(region)
    return streamingState.containsKey(cc) && !(streamingState[cc]?.fr?.isEmpty() ?: true)
}