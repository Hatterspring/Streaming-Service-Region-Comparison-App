package com.example.streamingserviceregioncomparisonapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
    Column {
        Text("Current Region")
        TextField(
            value=text,
            onValueChange = {
                text = it
                settingsViewModel.saveRegionName(text)
            }
        )
        Text("Background colour: ")
        Text("Foreground colour: ")
    }


}