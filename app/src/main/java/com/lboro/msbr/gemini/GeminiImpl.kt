package com.lboro.msbr.gemini

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.ServerException
import com.google.firebase.ai.type.generationConfig

class GeminiImpl: GeminiRepository {

    private val generativeModel = Firebase.ai(backend =
        GenerativeBackend.googleAI())
        .generativeModel(
            modelName = "gemini-2.5-flash",
            generationConfig = generationConfig {
                temperature = 0f
            },
            safetySettings = listOf(
                SafetySetting(
                    HarmCategory.HARASSMENT,
                    HarmBlockThreshold.LOW_AND_ABOVE
                ),
                SafetySetting(HarmCategory.HATE_SPEECH,
                    HarmBlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT,
                    HarmBlockThreshold.LOW_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT,
                    HarmBlockThreshold.LOW_AND_ABOVE),
            )
        )

    override suspend fun summariseMovieData(movieInfo: String, movie: String, region: String): String? {
        val prompt ="""
            You are producing a customer-facing summary of a large amount of data regarding movie services.
            Summarise the following data into:
             - all of the services available for $movie in $region.
             - three other regions, preferably those that offer all three services for $movie. list all services in that region and their type.
             
            $movieInfo
        """.trimIndent()
        try {
            return generativeModel.generateContent(prompt).text
        } catch (e: ServerException) {
            Log.e("Firebase Server Error", e.toString())
            return null
        }

    }

}