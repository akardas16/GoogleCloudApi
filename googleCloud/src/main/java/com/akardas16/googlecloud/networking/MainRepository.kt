package com.akardas16.googlecloud.networking

import com.akardas16.googlecloud.models.AudioRequest
import com.akardas16.googlecloud.models.AudioResponse
import com.akardas16.googlecloud.models.SpeechResponse
import com.akardas16.googlecloud.models.VoiceListResponse
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface MainRepository {

    suspend fun fetchAllVoices(result:(response: VoiceListResponse?, error:String?) -> Unit)
    suspend fun speak(body: AudioRequest, result:(response: AudioResponse?, error:String?) -> Unit)
    suspend fun speechToText(base64:String,audioMime:String,result:(response: SpeechResponse?, error:String?) -> Unit)


    companion object{
        fun create(key:String): MainRepositoryImpl {

            val httpClient = HttpClient(Android){
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }) // Example: Register JSON content transformation
                    // Add more transformations as needed for other content types

                    Gson()

                }

                install(HttpTimeout){//5 Minute
                    requestTimeoutMillis = 200000
                    connectTimeoutMillis = 200000
                    socketTimeoutMillis = 200000
                }
            }

           return MainRepositoryImpl(client = httpClient,key)
        }
    }

}