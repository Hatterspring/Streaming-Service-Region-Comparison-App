package com.lboro.msbr.ui.comparison

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lboro.msbr.BuildConfig
import com.lboro.msbr.data.codeToCountry
import com.lboro.msbr.data.database.MovieDetailsEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlinx.serialization.Serializable

class CompViewModel() : ViewModel() {
    /****************************************************
     VARIABLES
     ****************************************************/
    //movieState: keep track of the details for the searched movie.
    private val _movieState = MutableStateFlow(arrayOf(""))
    val movieState = _movieState.asStateFlow()

    //streamingState: keep track of the services that can
    //buy, rent or stream the movie for each region
    private val _serviceState = MutableStateFlow(mutableMapOf("" to ServiceByRegion(null, null, null)))
    val serviceState = _serviceState.asStateFlow()

    //typeState: keep track of whether the movie should be
    //bought, rented or streamed.
    val _typeState = MutableStateFlow(ServiceTypes.BUY)
    val typeState = _typeState.asStateFlow()

    private val _cachedState = MutableStateFlow(false)
    val cachedState = _cachedState.asStateFlow()

    //api information, courtesy of The Movie Database
    private val apiKey = BuildConfig.API_KEY
    private val accessToken = BuildConfig.ACCESS_TOKEN
    private val baseUrl = "https://api.themoviedb.org/3/"

    /****************************************************
     DATA
     ****************************************************/

    //Service by Region: A row of information
    //describing what movies may be bought, rented and
    //streamed in a given region
    @Serializable
    data class ServiceByRegion(
        val buy: List<String>? = null,
        val rent: List<String>? = null,
        val stream: List<String>? = null
    ) {
        override fun toString(): String {
            var out = "buy: "
            buy?.forEach { entry ->
                out += "|$entry"
            }
            out += "\nrent: "
            rent?.forEach { entry ->
                out += "|$entry"
            }
            out += "\nstream: "
            stream?.forEach { entry ->
                out += "|$entry"
            }
            return out
        }
    }

    //enum denoting types of service a movie may fall under
    enum class ServiceTypes {
        BUY,
        RENT,
        STREAM,
    }


    /****************************************************
     FUNCTIONS
     ****************************************************/

    /*
     Fetch Movie Details
     Inputs:
     * movie: String
     * context: Context
     Outputs:
     * none
     Process:
     * reset any previous searches for clarity
     * make an api call to search for the movie and turn
       the response into a readable format
     * parse the response into the necessary details
     * if there is a network connection problem, throw
       an error
     * store the parsed movie details in state
     * make a second api call to retrieve service
       information concerning the movie
     * parse the streaming information
     * store the streaming information in state
     * if the movie cannot be found, display a toast message
     */
    @Throws(MovieNotFoundException::class)
    fun fetchMovieDetails(movie: String, region: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _cachedState.update{false}
            _movieState.update { arrayOf("") }
            _serviceState.update { mutableMapOf("" to ServiceByRegion())}
            //val sanMovie = URLEncoder.encode(movie, StandardCharsets.UTF_8.toString())
            val movieJSON = getJSONFromApi(baseUrl+"search/movie?query=$movie")
            try {
                val movieDetails = parseMovieInfoJSON(movieJSON)
                val id = movieDetails[0]
                if ("Network Error! Please check the network connection!" in id) {
                    throw MovieNotFoundException(id)
                }
                _movieState.update({ movieDetails })
                Log.d("unobserved state", _movieState.value.toString())
                Log.i("uniform resource locator", baseUrl+"movie/$id/watch/providers")
                val streamingJSON = getJSONFromApi(baseUrl+"movie/$id/watch/providers")
                val streamingDetails = parseServiceInfoJSON(streamingJSON)
                sortRegionServices(region, streamingDetails)
                Log.i("streamingState", _serviceState.value.toString())
            } catch (e: MovieNotFoundException) {
                withContext(context=Dispatchers.Main){
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    fun fetchCachedMovieDetails(movieCache: List<MovieDetailsEntry>?, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _cachedState.update{true}
            _movieState.update { arrayOf("") }
            _serviceState.update { mutableMapOf("" to ServiceByRegion())}
            try {
                val movieDetails = parseCache(movieCache)!!
                val id = movieDetails[0]
                _movieState.update({ movieDetails })
                Log.d("unobserved state", _movieState.value.toString())
                val streamingDetails: MutableMap<String, ServiceByRegion> = parseServiceInfo(movieDetails[6])
                _serviceState.update {streamingDetails}
            } catch (e: MovieNotFoundException) {
                withContext(context=Dispatchers.Main){
                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    fun fetchProviderLink(context: Context) {
        var providerUrl = "https://www.themoviedb.org/movie/${_movieState.value[0]}".toUri()
        viewModelScope.launch {
            Log.i("providerUrl", providerUrl.toString())
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                providerUrl
            )
            context.startActivity(webIntent)
        }
    }

    fun parseCache(cache: List<MovieDetailsEntry>?): Array<String>? {
        var out = cache?.get(0)
        if (out != null) {
            return arrayOf(
                out.movie_id,
                out.name,
                out.description,
                out.image,
                out.release_date,
                out.rating,
                out.service_info
            )
        }
        return null
    }

    fun parseServiceInfo(info: String): MutableMap<String, ServiceByRegion> {
        var out = mutableMapOf("" to ServiceByRegion())
        var spl = info.split("{")
        spl.forEach { regionInfo ->
            var services = regionInfo.split("\n")
            var regionName = ""
            var buyList: List<String>? = mutableListOf("")
            var rentList: List<String>? = mutableListOf("")
            var streamList: List<String>? = mutableListOf("")
            services.forEach { service ->
                if (":" !in service) {
                    regionName = service
                } else {
                    var stuff = service.split("|")
                    when (stuff[0]) {
                        "buy: " -> {if (stuff.size > 1){buyList = stuff.subList(1, stuff.size)} else buyList = null}
                        "rent: " -> {if (stuff.size > 1){rentList = stuff.subList(1, stuff.size)} else rentList = null}
                        "stream: " -> {if (stuff.size > 1){streamList = stuff.subList(1, stuff.size)} else streamList = null}
                    }
                }
            }
            if (regionName != "") {
                out.put(regionName, ServiceByRegion(buyList,rentList,streamList))
            }

        }
        return out
    }

    /*
     Change Service Type
     Inputs:
     * st: ServiceTypes
     Outputs:
     * none
     Process:
     * update the typeState with the new Service Type
     */
    fun changeServiceType(st: ServiceTypes) {
        viewModelScope.launch(Dispatchers.IO) {
            _typeState.update { st }
            Log.d("typestate", typeState.value.toString())
        }
    }

    // The helper function that sends a GET request to the URL, returns the response in JSON string
    /*
     Get JSON from api
     Inputs:
     * url: String
     Outputs:
     * result: String
     Process:
     * establish a HTTPS connection using the input URL
     * add the access token to the header
     * take an input stream as a response and convert it
       to a String (in JSON format)
     * if this fails, the response will be a network error
     Acknowledgements:
     * This is a helper function from labs in COB155.
     */
    private fun getJSONFromApi(url: String): String {
        var result = ""
        var conn: HttpsURLConnection? = null
        try {
            val request = URL(url)
            Log.d("url", request.toString())
            conn = request.openConnection() as
                    HttpsURLConnection
            conn.setRequestProperty("Authorization", "Bearer $accessToken")
            conn.setRequestProperty("Accept", "application/json")
            Log.d("request", conn.toString())
            conn.connect()
            val inStream: InputStream = conn.inputStream
            Log.d("string", inStream.toString())
            result = convertInputStreamToString(inStream)
            Log.d("checkpoint", result)
        } catch (e: Exception) {
            Log.e("Network error", e.toString())
            result = "Network Error! Please check the network connection!"
        } finally {
            conn?.disconnect()
        }

        return result
    }

    /*
     Convert Input Stream to String
     Inputs:
     * inS: InputStream
     Outputs:
     * result: String
     Process:
     * build a String line by line using a StringBuilder
       and reading each line using a bufferedReader.
     Acknowledgements:
     * This is a helper function from labs in COB155.
     */
    @Throws(IOException::class)
    private fun convertInputStreamToString(inS: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inS))
        val result = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            result.append(line)
        }
        inS.close()
        return result.toString()
    }

    /*
     Parse Movie Information JSON
     Inputs:
     * json: String
     Outputs:
     * array of [id, title, overview, backdrop,
                 release date, vote average]
     Process:
     * if a network error occurred, only return the network error.
       since at first only the item at index 0 is checked, this
       will be caught.
     * create a JSON object from the string
     * obtain the top result from the search
     * obtain id, title, overview, backdrop, release date and vote
       average for this movie.
     */
    @Throws(MovieNotFoundException::class)
    fun parseMovieInfoJSON(json: String): Array<String>{
        if ("Network Error! Please check the network connection!" in json) {
            return arrayOf("Network Error! Please check the network connection!")
        }
        Log.i("json object", json)
        val jObject = JSONObject(json)
        val jArray = jObject.getJSONArray("results")
        if (jArray.length() == 0) {
            throw MovieNotFoundException("No movies from query!")
        }
        val dataObject = jArray.getJSONObject(0)
        val id = dataObject.getString("id")
        val title = dataObject.getString("title")
        val overview = dataObject.getString("overview")
        val poster = "https://image.tmdb.org/t/p/w500/${dataObject.getString("poster_path")}"
        val releasedate = dataObject.getString("release_date")
        val voteav = dataObject.getString("vote_average")
        return arrayOf(id, title, overview, poster, releasedate, voteav)
    }

    /*
     Parse Service Information JSON
     Inputs:
     * json: String
     Outputs:
     * out: Map<String, Streaming Service by Region>
     Process:
     * iterate through each entry in the results of the service query
     * for each entry, map the region to its available services
     */
    fun parseServiceInfoJSON(json: String): MutableMap<String,ServiceByRegion> {
        if ("Network Error! Please check the network connection!" in json) {
            throw MovieNotFoundException("Network Error! Please check the network connection!")
        }
        var out: MutableMap<String, ServiceByRegion> = mutableMapOf()
        val resultsObject = JSONObject(json).getJSONObject("results")
        val objIterator = resultsObject.keys().iterator()
        while (objIterator.hasNext()) {
            val region = objIterator.next()
            val info = resultsObject.getJSONObject(region)
            val country: String = codeToCountry(region) ?: "unknown country"
            if (country == "unknown country") {
                continue
            }
            out.put(country, parseRegionalStreamingInfo(info))
        }
        return out
    }

    fun sortRegionServices(region: String, map: MutableMap<String, ServiceByRegion>) {
        val out = map.toSortedMap()
        val current = out.get(region)
        out.remove(region)
        viewModelScope.launch {
            if (current != null) {
                val newMap: MutableMap<String, ServiceByRegion> = mutableMapOf("" to ServiceByRegion())
                newMap.put(region, current)
                out.forEach { key, value ->
                    newMap.put(key, value)
                }
                _serviceState.update { newMap }
            } else {
                _serviceState.update { map }
            }
            }
    }

    suspend fun sortRegionServices(map: MutableMap<String, ServiceByRegion>) {
        val out = map.toSortedMap()
        viewModelScope.launch {
            _serviceState.update { out }
        }
    }

    /*
     Parse Regional Streaming Information
     Inputs:
     * region: JSON Object
     Outputs:
     * Streaming Service By Region
     Process:
     * initialise a null safe list of strings for
       buying, renting and streaming
     * for each of these lists, add each corresponding
       provider name.
     * create and return a ServiceByRegion instance using these
       lists.
     */
    fun parseRegionalStreamingInfo(region: JSONObject): ServiceByRegion {
        var buy: MutableList<String> ?= mutableListOf()
        var rent: MutableList<String> ?= mutableListOf()
        var stream: MutableList<String> ?= mutableListOf()

        try {
            val buyArray: JSONArray = region.getJSONArray("buy")
            for (x in 0 until (buyArray.length())) {
                buy?.add("${buyArray.getJSONObject(x)?.get("logo_path").toString()}:${buyArray.getJSONObject(x)?.get("provider_name").toString()}")
            }
        } catch (e: JSONException) {
            Log.e("No buy section", e.toString())
        }

        try {
            val rentArray: JSONArray = region.getJSONArray("rent")
            for (y in 0 until (rentArray.length())) {

                rent?.add("${rentArray.getJSONObject(y)?.get("logo_path").toString()}:${rentArray.getJSONObject(y)?.get("provider_name").toString()}")
            }
        } catch (e: JSONException) {
            Log.e("No rent section", e.toString())
        }
        try {
            val streamArray: JSONArray = region.getJSONArray("flatrate")
            for (z in 0 until (streamArray.length())) {
                stream?.add("${streamArray.getJSONObject(z)?.get("logo_path").toString()}:${streamArray.getJSONObject(z)?.get("provider_name").toString()}")
            }
        } catch (e: JSONException) {
            Log.e("No streaming section", e.toString())
        }

        return ServiceByRegion(buy,rent,stream)
    }

    fun serviceDetailsToString(): String {
        var out = ""
        _serviceState.value.forEach { region, services ->
            out += "{$region\n${services}"
        }
        Log.i("viewModelToString", out)
        return out
    }

    fun isAvailable(region: String): Boolean {
        val ss = serviceState.value
        return when (_typeState.value){
            ServiceTypes.BUY -> ss.containsKey(region) && !(ss[region]?.buy?.isEmpty() ?: true)
            ServiceTypes.RENT -> ss.containsKey(region) && !(ss[region]?.rent?.isEmpty() ?: true)
            ServiceTypes.STREAM -> ss.containsKey(region) && !(ss[region]?.stream?.isEmpty() ?: true)
        }
    }

    fun saveSearch(): MovieDetailsEntry {
        val ms = _movieState.value
        return MovieDetailsEntry(
            movie_id = ms[0],
            name = ms[1],
            description = ms[2],
            image = ms[3],
            release_date = ms[4],
            rating = ms[5],
            service_info = serviceDetailsToString()
        )
    }
}