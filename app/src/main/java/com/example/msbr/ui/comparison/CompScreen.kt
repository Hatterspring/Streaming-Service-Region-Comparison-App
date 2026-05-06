package com.example.msbr.ui.comparison

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import com.example.msbr.data.codeToCountry
import com.example.msbr.data.countryToCode
import com.example.msbr.ui.settings.SettingsViewModel
import androidx.core.net.toUri
import com.example.msbr.data.database.ProviderEntry
import java.net.URI


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

    //break down the mapping of region to service
    val keys = serviceState.keys.toList()
    val vals = serviceState.values.toList()

    //retrieve the region from local storage
    val region = settingsViewModel.regionName.value

    fun insertProvData(){
        dbViewModel.getAllProviders()
    }

    /****************************************************
     STRUCTURE
     ****************************************************/
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
            text = try {
                (movieState[1]) + availableMessage(region, serviceState, typeState)
            } catch (e: ArrayIndexOutOfBoundsException) {
                "Movie not found..."
            }
        )
        Row {
            Button(
                onClick={compViewModel.changeServiceType(CompViewModel.ServiceTypes.BUY) }
            ) { Text("buy") }
            Button(
                onClick={
                    compViewModel.changeServiceType(CompViewModel.ServiceTypes.RENT)
                }
            ) { Text("rent") }
            Button(
                onClick={
                    compViewModel.changeServiceType(CompViewModel.ServiceTypes.STREAM)
                    insertProvData()
                }
            ) { Text("stream") }
        }
        LazyColumn (
            state = serviceListState,
            userScrollEnabled = true
        ){
            itemsIndexed(vals) { index, listContent ->
                when (typeState) {
                    CompViewModel.ServiceTypes.BUY ->  parseServiceInfo(listContent.buy, "buy", codeToCountry(keys[index]))
                    CompViewModel.ServiceTypes.RENT ->  parseServiceInfo(listContent.rent, "rent", codeToCountry(keys[index]))
                    CompViewModel.ServiceTypes.STREAM ->  parseServiceInfo(listContent.stream, "stream", codeToCountry(keys[index]))
                }?.let {
                    Text(
                        text =
                            it,
                        modifier = Modifier.clickable(onClick = {
                            val webIntent = Intent(Intent.ACTION_VIEW, "https://www.netflix.com".toUri())
                            context.startActivity(webIntent)
                        })
                    )
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
        }
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
    if (!(region?.isEmpty() ?: true) && isAvailable(region, service, type)) {
        return " is available to stream in your region!"
    } else {
        return " is not available to stream in your region."
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

//TODO: change to lazyColumn or drop-down if possible and remove this function

/*
     Parse Service Information
     Inputs:
     * serviceInfo: null-safe list of strings
     Outputs:
     * Boolean
     Process:
     * get the country code of the region and check if
       the corresponding type of service has any matches.
     */
fun parseServiceInfo(serviceInfo: List<String>?, type: String, region: String?): String? {
    var out = "$region - available to $type: "
    if (serviceInfo != null && !serviceInfo.isEmpty()) {
        for (service in serviceInfo) {
            out += "$service  | "
        }
    } else return null
    return out
}

