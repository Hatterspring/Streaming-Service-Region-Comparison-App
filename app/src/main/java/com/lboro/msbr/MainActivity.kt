package com.lboro.msbr

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lboro.msbr.ui.theme.MovieServiceByRegionTheme
import com.lboro.msbr.ui.Comparison


class MainActivity : ComponentActivity() {

    private lateinit var fusedLocClient: FusedLocationProviderClient


    //TODO: handle app lifecycle
    @Preview(showBackground=true)
    @Composable
    fun ComparisonPreview() {
        Comparison(fusedLocClient)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        setContent {
            MovieServiceByRegionTheme {
                Comparison(fusedLocClient)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v("Lifecycle methods","onStart")
    }

    override fun onPause() {
        super.onPause()
        Log.v("Lifecycle methods","onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("Lifecycle methods","onDestroy")
    }
}


