package com.example.streamingserviceregioncomparisonapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun CompScreen(
    navController: NavController,
    compViewModel: CompViewModel,
    modifier: Modifier
) {
    val movieState by compViewModel.movieState.collectAsState()
    val streamingState by compViewModel.streamingState.collectAsState()
    val listState = rememberLazyListState()
    Text(movieState[0])
    LazyColumn (
        state = listState
    ){
        itemsIndexed(movieState) { index, listContent ->
            Text(listContent)
        }
    }

}