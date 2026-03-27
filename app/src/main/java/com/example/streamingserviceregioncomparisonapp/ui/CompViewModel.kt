package com.example.streamingserviceregioncomparisonapp.ui;

//https://api.themoviedb.org/3/movie/244786/watch/providers

import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection
import kotlin.arrayOf

class CompViewModel() : ViewModel() {
    private val _movieState = MutableStateFlow(arrayOf(""))
    val movieState = _movieState.asStateFlow()
    private val _streamingState = MutableStateFlow(arrayOf(""))
    val streamingState = _streamingState.asStateFlow()
    private val apiKey = BuildConfig.API_KEY
    private val accessToken = BuildConfig.ACCESS_TOKEN
    private val baseUrl = "https://api.themoviedb.org/3/"

    fun fetchMovieDetails(movie: String) {
        /*ContextCompat.checkSelfPermission(,
            Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)*/
        viewModelScope.launch(Dispatchers.IO) {
            val movieJSON = getResponse(baseUrl+"search/movie?query=$movie")
            val movieDetails = parseMovieInfoJSON(movieJSON)
            _movieState.update ({ movieDetails })
            Log.d("unobserved state", _movieState.value.toString())
            /*val id = movieDetails[0]
            val streamingJSON = getResponse(baseUrl+"movie/$id/watch/providers")
            val streamingDetails = parseStreamingInfoJSON(streamingJSON)*/
        }
    }

    private fun getResponse(url:String): String {
        //build the URL using the user entered movie
//        val movieEnc = URLEncoder.encode(movie)
        Log.d("getMovie url", url)
        try {
            val response = getJSONFromApi(url)
            Log.d("getMovie",response)
            return response
        }catch (e: Exception) {
            e.printStackTrace()
            Log.e("random error", e.toString())
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
            conn.setRequestProperty("Authorization", "Bearer $accessToken")
            conn.setRequestProperty("Accept", "application/json")
            conn.connect()
            val inStream: InputStream = conn.inputStream
            result = convertInputStreamToString(inStream)
            Log.d("checkpoint", result)
        } catch (e: Exception) {
            e.printStackTrace()
            result = "Network Error! Please check the network connection!"
        } finally {
            conn?.disconnect()
        }

        return result //returns the fetched JSON string
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

    fun parseMovieInfoJSON(json: String): Array<String>{
        val jObject = JSONObject(json)
        val jArray = jObject.getJSONArray("results")
        val dataObject = jArray.getJSONObject(1)
        val id = dataObject.getString("id")
        val title = dataObject.getString("title")
        val overview = dataObject.getString("overview")
        val backdrop = dataObject.getString("backdrop_path")
        val releasedate = dataObject.getString("release_date")
        val voteav = dataObject.getString("vote_average")
        return arrayOf(id, title, overview, backdrop, releasedate, voteav)
    }

    fun parseStreamingInfoJSON(json: String): Map<String,List<String>> {
        var out: MutableMap<String, List<String>> = mutableMapOf()
        val resultsObject = JSONObject(json).getJSONObject("results")



        return out
    }

}
