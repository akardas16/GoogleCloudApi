package com.akardas16.googlecloud.speechrecognition

import com.google.cloud.speech.v1.StreamingRecognizeResponse
import kotlinx.coroutines.flow.Flow


interface VoiceRecognitionRepository {

    val recognitionResponse: Flow<StreamingRecognizeResponse>
    fun startRecognition()
    fun resume()
    fun pause()
    fun release()


}