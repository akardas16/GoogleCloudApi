package com.akardas16.googlecloud

import com.akardas16.googlecloud.speechrecognition.VoiceRecognitionGoogleApiRepository
import com.google.api.gax.core.CredentialsProvider
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.StreamingRecognizeResponse
import com.google.cloud.speech.v1.stub.GrpcSpeechStub
import com.google.cloud.speech.v1.stub.SpeechStubSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.InputStream
import java.nio.charset.StandardCharsets


class VoiceRecognition(
    private val credentials: String,
    private val coroutineScope: CoroutineScope,
    private val languageCode: String = "en-US",
    private val sampleRate: Int = 16000,
) {

    private val voiceRecognizer = getRepo()
    val recognitionResponse: Flow<StreamingRecognizeResponse> = voiceRecognizer.recognitionResponse



    private fun getRepo(): VoiceRecognitionGoogleApiRepository {

        val stream: InputStream = credentials.byteInputStream(StandardCharsets.UTF_8)
        val credential = CredentialsProvider {
            ServiceAccountCredentials.fromStream(stream)
        }
        var grpcStub: GrpcSpeechStub? = null
        SpeechStubSettings.newBuilder()?.apply {
            credentialsProvider = credential
            endpoint = "speech.googleapis.com:443"
            grpcStub = GrpcSpeechStub.create(build())
        }
        val speechClient = SpeechClient.create(grpcStub)


        return VoiceRecognitionGoogleApiRepository(languageCode = languageCode, sampleRate = sampleRate,speechClient,
            CoroutineScope(SupervisorJob() + Dispatchers.Default)
        )
    }

    fun observeFinalText(result:(text:String) -> Unit){
        coroutineScope.launch {
            recognitionResponse.collect {response->
                response.let {
                    var text: String? = null
                    val isFinal: Boolean
                    if (response.resultsCount > 0) {
                        val result1 = response.getResults(0)
                        isFinal = result1.isFinal
                        if (result1.alternativesCount > 0) {
                            val alternative = result1.getAlternatives(0)
                            text = alternative.transcript
                            text?.let {
                                if (isFinal) result(it)
                            }

                        }
                    }

                }
            }
        }
    }

    fun observeResponse(result:(response:StreamingRecognizeResponse) -> Unit){
        coroutineScope.launch {
            recognitionResponse.collect {response->
                result(response)
            }
        }
    }

    fun startRecognition(){
        voiceRecognizer.startRecognition()
    }

    fun releaseRecognition() {
        voiceRecognizer.release()
    }
}