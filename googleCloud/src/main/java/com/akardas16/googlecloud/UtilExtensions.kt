package com.akardas16.googlecloud

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Base64
import android.widget.Toast
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.StringWriter
import java.io.Writer
import java.util.regex.Pattern


fun Context.showToast(message: String, mode: Duration = Duration.LONG) = Toast.makeText(
    this, message,
    if (mode == Duration.LONG) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
).show()

enum class Duration {
    LONG, SHORT
}

fun Context.decodeAndSaveAudio(base64AudioString: String): File {
    val decodedBytes: ByteArray = Base64.decode(base64AudioString, Base64.DEFAULT)
    val file = File(cacheDir, "gCloudAudio.mp3") // Change the file name and extension if needed
    val outputStream = FileOutputStream(file)
    outputStream.write(decodedBytes)
    outputStream.close()
    return file
}

fun findLangCode(voiceType: String): String {
    val matcher = Pattern.compile("-").matcher(voiceType)
    val occurrences = 2
    for (i in 0 until occurrences) matcher.find()
    return voiceType.substring(0, matcher.start())
}

fun encodeAudioToBase64(file: File): String {
    val inputStream = FileInputStream(file)
    val bytes = ByteArray(file.length().toInt())
    inputStream.read(bytes)
    inputStream.close()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

fun playAudio(audioFile: File) {
    val mediaPlayer = MediaPlayer()
    try {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setAudioAttributes(attributes)
        mediaPlayer.setDataSource(audioFile.path)
        mediaPlayer.prepare()
        mediaPlayer.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun rawToString(context: Context, file: Int): String {
    val inputStream: InputStream = context.resources.openRawResource(file)
    val reader: Reader = BufferedReader(InputStreamReader(inputStream, "utf-8"))

    val writer: Writer = StringWriter()
    val buffer = CharArray(1024)
    reader.use { it ->
        var n: Int
        while (it.read(buffer).also { n = it } != -1) {
            writer.write(buffer, 0, n)
        }
    }
    return writer.toString()
}
