package com.akardas16.googlecloud.networking

import com.akardas16.googlecloud.models.Audio
import com.akardas16.googlecloud.models.AudioRequest
import com.akardas16.googlecloud.models.AudioResponse
import com.akardas16.googlecloud.models.Config
import com.akardas16.googlecloud.models.SpeechRequest
import com.akardas16.googlecloud.models.SpeechResponse
import com.akardas16.googlecloud.models.VoiceListResponse
import com.akardas16.googlecloud.networking.MainRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MainRepositoryImpl(private val client: HttpClient,private val key: String) :
    MainRepository {


    override suspend fun fetchAllVoices(result: (response: VoiceListResponse?, error: String?) -> Unit) {
        try {
            val httpResponse = client.get {
                headers {
                    append("Content-Type","application/json")
                }
                setBody(body)
                url("https://texttospeech.googleapis.com/v1beta1/voices?key=$key")
            }
            if (httpResponse.status.value in 200..299){
                result(httpResponse.body<VoiceListResponse>(),null)
            }else{
                result(null,"Error Http Status Code --> ${httpResponse.status.value} \nError body Text--> ${httpResponse.bodyAsText()}")
            }

        }catch (e:Exception){
            result(null,"\nException--> ${e.message}")
        }
    }

    override suspend fun speak(body: AudioRequest, result: (response: AudioResponse?, error: String?) -> Unit) {
        try {
            val httpResponse = client.post {
                contentType(ContentType.Application.Json)
                setBody(body)
                url("https://texttospeech.googleapis.com/v1beta1/text:synthesize?alt=json&key=$key")

            }
            if (httpResponse.status.value in 200..299){
                result(httpResponse.body<AudioResponse>(),null)
            }else{
                result(null,"Error Http Status Code --> ${httpResponse.status.value} \nError body Text--> ${httpResponse.bodyAsText()}")
            }

        }catch (e:Exception){
            result(null,"\nException--> ${e.message}")
        }
    }

    override suspend fun speechToText(
        base64: String, audioMime:String,
        result: (response: SpeechResponse?, error: String?) -> Unit,
    ) {

        val body = SpeechRequest(audio = Audio(content = base64),
            config = Config(languageCode = "en-US", encoding = audioMime, sampleRateHertz = 16000)
        )
        try {
            val httpResponse = client.post {
                contentType(ContentType.Application.Json)
                setBody(body)
                url("https://speech.googleapis.com/v1/speech:recognize?alt=json&key=$key")

            }
            if (httpResponse.status.value in 200..299){
                result(httpResponse.body<SpeechResponse>(),null)
            }else{
                result(null,"Error Http Status Code --> ${httpResponse.status.value} \nError body Text--> ${httpResponse.bodyAsText()}")
            }

        }catch (e:Exception){
            result(null,"\nException--> ${e.message}")
        }
    }


}