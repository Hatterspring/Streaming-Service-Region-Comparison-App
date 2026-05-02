package com.example.streamingserviceregioncomparisonapp.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streamingserviceregioncomparisonapp.ui.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    modifier: Modifier
) {
    val region by settingsViewModel.regionName.observeAsState("United Kingdom")
    var text by remember { mutableStateOf(region) }
    val attempt = settingsViewModel.countryIsValid.collectAsState(true)
    val context = LocalContext.current
    var updateCount by remember { mutableIntStateOf(0) }
    Column {
        Text("Current Region")
        TextField(
            value=text,
            onValueChange = {
                text = it
            }
        )

        Text("Background colour: ")
        Text("Foreground colour: ")

        Button(onClick={
            settingsViewModel.saveRegionName(text)
            updateCount++
        }) {
            Text("Save Region")
            LaunchedEffect(updateCount) {
                if (attempt.value){
                    Toast.makeText(context,"Region saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context,"Region not valid. Please enter a valid region.", Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}