package com.example.streamingserviceregioncomparisonapp.ui.comparison

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.streamingserviceregioncomparisonapp.data.codeToCountry
import com.example.streamingserviceregioncomparisonapp.data.countryToCode
import com.example.streamingserviceregioncomparisonapp.ui.settings.SettingsViewModel
import androidx.core.net.toUri


@Composable
fun CompScreen(
    navController: NavController,
    compViewModel: CompViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {
    val movieState by compViewModel.movieState.collectAsState()
    val streamingState by compViewModel.streamingState.collectAsState()
    val typeState by compViewModel.typeState.collectAsState()
    val movieListState = rememberLazyListState()
    val streamingListState = rememberLazyListState()
    val keys = streamingState.keys.toList()
    val vals = streamingState.values.toList()
    val region = settingsViewModel.regionName.value
    val context = LocalContext.current
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
        Row {
            Button(
                onClick={compViewModel.changeServiceType(CompViewModel.movieTypes.BUY) }
            ) { Text("buy") }
            Button(
                onClick={compViewModel.changeServiceType(CompViewModel.movieTypes.RENT)}
            ) { Text("rent") }
            Button(
                onClick={compViewModel.changeServiceType(CompViewModel.movieTypes.STREAM)}
            ) { Text("stream") }
        }
        LazyColumn (
            state = streamingListState,
            userScrollEnabled = true
        ){
            itemsIndexed(vals) { index, listContent ->
                when (typeState) {
                    CompViewModel.movieTypes.BUY ->  parseServiceInfo(listContent.b, "buy", codeToCountry(keys[index]))
                    CompViewModel.movieTypes.RENT ->  parseServiceInfo(listContent.r, "rent", codeToCountry(keys[index]))
                    CompViewModel.movieTypes.STREAM ->  parseServiceInfo(listContent.fr, "stream", codeToCountry(keys[index]))
                }?.let {
                    Text(
                        text =
                            it,
                        modifier = Modifier.clickable(onClick = {
                            val location = "Netflix"
                            //construct the uri
                            val webIntent: Intent = Intent(Intent.ACTION_VIEW, "https://www.netflix.com".toUri())
                            //if there is an app that can handle the implicit intent
                            context.startActivity(webIntent)
                            /*
                            if (webIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(webIntent)
                            }*/
                        })
                    )
                }
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

fun parseServiceInfo(serviceInfo: List<String>?, type: String, region: String?): String? {
    var out = "$region - available to $type: "
    if (serviceInfo != null && !serviceInfo.isEmpty()) {
        for (service in serviceInfo) {
            out += "$service  | "
        }
    } else return null
    return out
}