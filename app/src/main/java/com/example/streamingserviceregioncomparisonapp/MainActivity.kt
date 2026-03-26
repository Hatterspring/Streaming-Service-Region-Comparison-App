package com.example.streamingserviceregioncomparisonapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.streamingserviceregioncomparisonapp.ui.Comparison
import com.example.streamingserviceregioncomparisonapp.ui.theme.StreamingServiceRegionComparisonAppTheme

/*val keystoreFile = project.rootProject.file("apikeys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())
        val apiKey =  properties.getProperty("tmdbApiKey") ?: ""
        buildConfigField(type="String",name="API_KEY",value="\"$apiKey\"")

        val accessToken = properties.getProperty("tmdbAccessToken") ?: ""
        buildConfigField(type="String",name="ACCESS_TOKEN", value="\"$accessToken\"") */
class MainActivity : ComponentActivity() {

    @Preview(showBackground=true)
    @Composable
    fun ComparisonPreview() {
        Comparison()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StreamingServiceRegionComparisonAppTheme {
                /*Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )

                }*/
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

/*@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}*/



/*@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StreamingServiceRegionComparisonAppTheme {
        Greeting("Android")
    }
}*/



