package com.example.streamingserviceregioncomparisonapp.ui

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


@Composable
fun CompScreen(
    navController: NavController,
    compViewModel: CompViewModel,
    modifier: Modifier
) {
    val movieState by compViewModel.movieState.collectAsState()
    val streamingState by compViewModel.streamingState.collectAsState()
    val movieListState = rememberLazyListState()
    val streamingListState = rememberLazyListState()
    val keys = streamingState.keys.toList()
    val vals = streamingState.values.toList()
    Text(movieState[0])
    Column {
        LazyColumn (
            state = movieListState
        ){
            itemsIndexed(movieState) { index, listContent ->
                Text(listContent)
            }
        }
        LazyColumn (
            state = streamingListState
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
    }


}