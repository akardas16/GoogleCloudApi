package com.akardas16.googlecloud

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import com.akardas16.googlecloud.enums.DeviceType
import com.akardas16.googlecloud.models.AudioConfig
import com.akardas16.googlecloud.models.AudioRequest
import com.akardas16.googlecloud.models.Input
import com.akardas16.googlecloud.models.SpeechResponse
import com.akardas16.googlecloud.models.Voice
import com.akardas16.googlecloud.models.VoiceListResponse
import com.akardas16.googlecloud.networking.MainRepository
import java.io.File


class GCloudAPI(private val context: Context, key:String) {

    private  var repository: MainRepository = MainRepository.create(key = key)



    suspend fun getVoiceList(result: (response: VoiceListResponse?, error: String?) -> Unit) =
        repository.fetchAllVoices(result)


    suspend fun speak(
        input: String,
        name: String = "en-US-Wavenet-F",
        deviceType: DeviceType = DeviceType.HEADPHONES,
        pitch: Long = 0,
        speakingRate: Long = 1,
        result: (uri: Uri?, error: String?) -> Unit,
    ){

        val requestData = AudioRequest(audioConfig = AudioConfig(audioEncoding = "LINEAR16",
            effectsProfileID = listOf(deviceType.getDeviceProfile()),pitch,speakingRate),
            input = Input(input),
            voice = Voice(languageCode = findLangCode(name), name = name)
        )
        repository.speak(requestData){ response, error ->

            response?.let {
                val audioFile = context.decodeAndSaveAudio(it.audioContent)
                result(Uri.fromFile(audioFile),null)

            }
            error?.let {
                result(null,it)
            }
        }
    }

    suspend fun speakNow(
        input: String,
        name: String = "en-US-Wavenet-F",
        deviceType: DeviceType = DeviceType.HEADPHONES,
        pitch: Long = 0,
        speakingRate: Long = 1,
        onStarted: () -> Unit = {},
        onCompleted: () -> Unit = {},
        onError: (error: String) -> Unit = {}
    ) {

        val requestData = AudioRequest(audioConfig = AudioConfig(audioEncoding = "LINEAR16",
            effectsProfileID = listOf(deviceType.getDeviceProfile()),pitch,speakingRate),
            input = Input(input),
            voice = Voice(languageCode = findLangCode(name), name = name)
        )
        repository.speak(requestData){ response, error ->

            response?.let {
                val audioFile = context.decodeAndSaveAudio(it.audioContent)
                val mediaPlayer =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) MediaPlayer(context) else MediaPlayer()
                try {
                    mediaPlayer.setDataSource(context,Uri.fromFile(audioFile))
                    mediaPlayer.prepare()
                    onStarted()
                    mediaPlayer.start()
                }catch (e:Exception){
                    e.localizedMessage?.let { it1 -> onError(it1) }
                }
                mediaPlayer.setOnCompletionListener {
                    onCompleted()
                }

            }
            error?.let {
                onError(it)
            }
        }
    }

    suspend fun audioToText(file: File,audioMime:String = "MP3",result:(response: SpeechResponse?, error:String?) -> Unit) {
        val base64 = encodeAudioToBase64(file)
        repository.speechToText(base64 = base64, audioMime = "MP3", result = result)
    }
}


