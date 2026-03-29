package com.example.streamingserviceregioncomparisonapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    modifier: Modifier
) {
    val region by settingsViewModel.regionName.observeAsState("United Kingdom")
    var text by remember { mutableStateOf(region) }
    val attempt = settingsViewModel.countryIsValid.collectAsState(true)
    Column {
        Text("Current Region")
        TextField(
            value=text,
            onValueChange = {
                text = it
            }
        )
        Text(
            text=(if (attempt.value){
                ""
            }else{
                "This country is not valid!"
            })
        )
        Text("Background colour: ")
        Text("Foreground colour: ")

        Button(onClick={
            settingsViewModel.saveRegionName(text)
        }) {
            Text("Save Region")
        }

    }


}