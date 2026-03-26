package com.example.streamingserviceregioncomparisonapp.ui;

import android.util.Log
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import com.example.streamingserviceregioncomparisonapp.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class CompViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow("")
    val uiState = _uiState.asStateFlow()
    private val apiKey = BuildConfig.API_KEY
    private val accessToken = BuildConfig.ACCESS_TOKEN

    fun fetchMovieDetails(movie: String) {
        /*ContextCompat.checkSelfPermission(,
            Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)*/
        viewModelScope.launch(Dispatchers.IO) {
            //val moviejson = getMovie(movie)
            //Log.i("json", moviejson)
            _uiState.update { getMovieJSON(movie) }
            Log.d("unobserved state", _uiState.value)
        }
    }

    private fun getMovieJSON(movie:String): String {
        //build the URL using the user entered movie
        val url =
            "https://api.themoviedb.org/3/movie/11"
        Log.d("getMovie url", url)
        try {
            val response = getJSONFromApi(url)
            Log.d("getMovie",response)
            //val cityArray = JSONArray(response)
            //Log.d("success!",cityArray.toString())
            return response
        }catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("unsuccessful", "message wasn't committed to state")
        return ""
    }

    // The helper function that sends a GET request to the URL, returns the response in JSON string
    private fun getJSONFromApi(url: String): String {
        var result = ""
        var conn: HttpsURLConnection? = null
        try {
            val request = URL(url)
            conn = request.openConnection() as
                    HttpsURLConnection
            result =
                convertInputStreamToString(sendApiRequest(url, conn))
            //parseJSON(result)
        } catch (e: Exception) {
            e.printStackTrace()
            result = "Network Error! Please check the network connection!"
        } finally {
            conn?.disconnect()
        }

        return result //returns the fetched JSON string
    }

    private fun sendApiRequest(url: String, conn: HttpsURLConnection): InputStream {
        conn.setRequestProperty("Authorization", "Bearer $accessToken")
        Log.d("connection", conn.toString())
        conn.connect()
        return conn.inputStream
    }

    // The helper function that converts the input stream to String
    @Throws(IOException::class)
    private fun convertInputStreamToString(inS: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inS))
        val result = StringBuilder()
        var line: String?
        // Read out the input stream buffer line by line until it's empty
        while (bufferedReader.readLine().also { line = it } != null) {
            result.append(line)
        }
        // Close the input stream and return
        inS.close()
        return result.toString()
    }

    fun parseJSON(json: String){
        val jObject = JSONObject(json)
        Log.d("dataObjectInfo", "got here1")
        //val jArray = jObject.getJSONArray("adult")
        Log.d("dataObjectInfo", "got here2")
        //val dataObject = jArray.getJSONObject(1)
        Log.d("dataObjectInfo", "got here3")
        val value1 = jObject.getString("adult")
        Log.i("dataObject", value1)
    }
}
