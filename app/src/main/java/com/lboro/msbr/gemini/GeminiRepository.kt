package com.lboro.msbr.gemini

interface GeminiRepository {

    suspend fun summariseMovieData(movieInfo: String, movie: String, region: String): String?

}