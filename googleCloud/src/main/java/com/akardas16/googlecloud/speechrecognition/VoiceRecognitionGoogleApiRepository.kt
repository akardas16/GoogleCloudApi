package com.akardas16.googlecloud.speechrecognition

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.google.api.gax.rpc.ClientStream
import com.google.api.gax.rpc.ResponseObserver
import com.google.api.gax.rpc.StreamController
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechContext
import com.google.cloud.speech.v1.StreamingRecognitionConfig
import com.google.cloud.speech.v1.StreamingRecognizeRequest
import com.google.cloud.speech.v1.StreamingRecognizeResponse
import com.google.protobuf.ByteString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class VoiceRecognitionGoogleApiRepository(languageCode:String = "en-US",sampleRate:Int = 16000,
    private val speechClient: SpeechClient,
    private val coroutineScope: CoroutineScope
) : VoiceRecognitionRepository {


    companion object{
        const val TAG = "VoiceRecognition"
        const val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT
        const val BUFFER_SIZE: Int = 6400


    }


    private var clientStream: ClientStream<StreamingRecognizeRequest>? = null
    private var audioRecord: AudioRecord? = null
    private var isStreaming = true

    override val recognitionResponse: Flow<StreamingRecognizeResponse> = callbackFlow {
        val callback = object : ResponseObserver<StreamingRecognizeResponse> {
            override fun onStart(controller: StreamController?) {
                Log.i(TAG, "ResponseObserver.onStart")
            }

            override fun onResponse(response: StreamingRecognizeResponse?) {
                Log.i(TAG, "ResponseObserver.onResponse($response)")
                response?.let {
                    trySend(it)


                }

            }

            override fun onError(t: Throwable?) {
                Log.e(TAG, "onError[${t?.message}]")
            }

            override fun onComplete() {
                Log.i(TAG, "onComplete $clientStream $audioRecord")
            }
        }


        clientStream = speechClient.streamingRecognizeCallable()?.splitCall(callback)
        clientStream?.send(startRequest)

        awaitClose {
            audioRecord?.release()
            clientStream?.closeSend()
            audioRecord = null
            clientStream = null
        }
    }.shareIn(coroutineScope, SharingStarted.Lazily, replay = 0)


    override fun startRecognition() {
        startRecording()
    }

    private fun startRecording() {
        if (audioRecord != null) return
        coroutineScope.launch {
            var minBufferSize = BUFFER_SIZE
            try {
                val buffer = ByteArray(minBufferSize)
                initVoiceRecorder()
                audioRecord?.apply {
                    startRecording()
                    while (true) {
                        minBufferSize = read(buffer, 0, buffer.size)
                        recognize(buffer, minBufferSize)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun recognize(data: ByteArray?, size: Int) {
        if (!isStreaming) return
        clientStream?.send(
            StreamingRecognizeRequest.newBuilder()
                .setAudioContent(ByteString.copyFrom(data, 0, size)).build()
        )
    }

    override fun resume() {
        isStreaming = true
    }

    override fun pause() {
        isStreaming = false
    }

    private val sampleRate: Int
        get() = audioRecord?.sampleRate ?: 0

    private val startRequest = StreamingRecognizeRequest.newBuilder()
        .setStreamingConfig(
            StreamingRecognitionConfig.newBuilder()
                .setConfig(
                    RecognitionConfig.newBuilder()
                        .addSpeechContexts(
                            SpeechContext.newBuilder()
                               // .addAllPhrases(VoiceCommandEntity.getAllCommands())
                        )
                        .setLanguageCode(languageCode)
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(sampleRate)
                        .build()
                )
                .setInterimResults(true)
                .setSingleUtterance(false)
                .build()
        ).build()

    @SuppressLint("MissingPermission")
    private fun initVoiceRecorder() = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        CHANNEL_CONFIG,
        AUDIO_FORMAT,
        BUFFER_SIZE * 10
    ).also {
        audioRecord = it
    }

    override fun release() {
        audioRecord?.release()
        audioRecord = null
        clientStream?.closeSend()
        clientStream = null
    }
}