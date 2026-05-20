package com.lboro.msbr.ui.settings

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.lboro.msbr.ui.DataViewModel
import com.lboro.msbr.ui.theme.Black
import com.lboro.msbr.ui.theme.Red
import com.lboro.msbr.ui.theme.White
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    dataViewModel: DataViewModel,
    locationClient: FusedLocationProviderClient?,
    modifier: Modifier
) {
    /****************************************************
     VARIABLES
     ****************************************************/
    //establish context
    val context = LocalContext.current

    //collect state from view model
    val region by dataViewModel.regionName.observeAsState()

    //collect other helpful state
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    //establish state for the current region and updates to it
    var text by remember {mutableStateOf(region)}

    val loc = ACCESS_COARSE_LOCATION

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getLocation() {
        locationClient?.getLastLocation()
            ?.addOnSuccessListener{ loc ->
                if (loc != null) {
                    Log.i("location information", loc.toString())
                    val lat = loc.latitude
                    val lon = loc.longitude
                    dataViewModel.getRegionByCoords(lat,lon) {r ->
                        Log.i("region saved", r!!)
                        text = r
                        coroutineScope.launch {
                            var success: Boolean
                            try {
                                success = text?.let { dataViewModel.saveRegionName(it) } ?: false
                            } catch (e: NullPointerException) {
                                success = false
                            }
                            if (success){
                                Toast.makeText(context,"Region saved!", Toast.LENGTH_SHORT).show()
                                Log.i("region saved", "yes")
                            } else {
                                Toast.makeText(context,"Unable to find location. Please enter a region.", Toast.LENGTH_LONG).show()
                                Log.i("region saved", "no")
                            }
                        }
                    }
                } else {
                    Toast.makeText(context,"Unable to find location. Please enter a region.", Toast.LENGTH_LONG).show()
                    Log.i("region saved", "no")
                }
            }
    }

    val permRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (dataViewModel.onPermissionChange(loc, granted)) {
            getLocation()
        } else {
            Toast.makeText(context, "Location currently disabled due to denied permission.", Toast.LENGTH_LONG).show()
        }
    }


    /****************************************************
     STRUCTURE
     ****************************************************/
    Column(
        modifier = Modifier
            .padding(all=24.dp)
    ) {
        Text("Current Region: $region",
                modifier = Modifier
                .padding( bottom = 10.dp)
            )
        TextField(
            value=text ?: "",
            label = {Text(text="Change Region")},
            onValueChange = {
                text = it
            },
            shape = RoundedCornerShape(40.dp),

            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Black,
                unfocusedContainerColor = Black,
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedLabelColor = Red,
                unfocusedLabelColor = White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .padding( horizontal = 10.dp)
        )

        Row() {
            Button(onClick={

                coroutineScope.launch {
                    val success = text?.let { dataViewModel.saveRegionName(it) } ?: false
                    if (success){
                        Toast.makeText(context,"Region saved!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context,"Region not valid. Please enter a valid region.", Toast.LENGTH_LONG).show()
                    }
                }

                },
                modifier = Modifier.padding( 10.dp)
            ) {
                Text("Save Region")
            }

            Button(
                onClick = {
                    if (hasPermission(loc)){
                        getLocation()
                    } else {
                        permRequest.launch(loc)
                    }
                },
                modifier = Modifier.padding( 10.dp)
            ) {
                Text("Use Last Location")
            }
        }
    }
}