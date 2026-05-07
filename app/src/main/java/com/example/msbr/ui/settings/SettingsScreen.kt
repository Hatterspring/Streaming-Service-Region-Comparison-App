package com.example.msbr.ui.settings

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
    modifier: Modifier
) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //establish context
    val context = LocalContext.current

    //collect state from view model
    val region by settingsViewModel.regionName.observeAsState("United Kingdom")
    val attempt = settingsViewModel.countryIsValid.collectAsState(true)

    //collect other helpful state
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    //establish state for the current region and updates to it
    var text by remember { mutableStateOf(region) }
    var updateCount by remember { mutableIntStateOf(0) }

    /****************************************************
     FUNCTIONS
     ****************************************************/
    /*
     Has Permission
     Inputs:
     * permission: String
     Outputs:
     * Boolean
     Process:
     * check if, given the context, a permission is granted
     */
    fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    //check if location permissions are granted when launched
    val requestLocationPermission =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                settingsViewModel.onPermissionChange(ACCESS_COARSE_LOCATION, hasPermission(ACCESS_COARSE_LOCATION))
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Location currently disabled due to denied permission.")
                }
            }
        }

    /****************************************************
     STRUCTURE
     ****************************************************/
    Column(
        modifier = Modifier
            .padding(all=20.dp)
    ) {
        Text("Current Region")
        TextField(
            value=text,
            onValueChange = {
                text = it
            }
        )
        Row() {
            Button(onClick={
                settingsViewModel.saveRegionName(text)
                updateCount++ //used to trigger LaunchedEffect
                },
                modifier = Modifier.padding(end = 5.dp)
            ) {
                Text("Save Region")
                LaunchedEffect(updateCount) {
                    if (attempt.value){
                        Toast.makeText(context,"Region saved!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context,"Region not valid. Please enter a valid region.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            //Use the current location to save a region
            Button(
                onClick = {requestLocationPermission.launch(ACCESS_COARSE_LOCATION)}
            ) {
                Text("Use Current Location")
            }
        }




        //wip
        Text("Background colour: ")
        Text("Foreground colour: ")

    }
}