package com.example.msbr

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.msbr.ui.theme.MovieServiceByRegionTheme
import com.example.msbr.ui.Comparison


class MainActivity : ComponentActivity() {


    //TODO: handle app lifecycle
    @Preview(showBackground=true)
    @Composable
    fun ComparisonPreview() {
        Comparison()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieServiceByRegionTheme {
                Comparison()
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


