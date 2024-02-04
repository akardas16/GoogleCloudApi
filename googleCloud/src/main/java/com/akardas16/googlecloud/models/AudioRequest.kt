package com.akardas16.googlecloud.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AudioRequest (
    val audioConfig: AudioConfig,
    val input: Input,
    val voice: Voice
)

@Serializable
data class AudioConfig (
    val audioEncoding: String,

    @SerialName("effectsProfileId")
    val effectsProfileID: List<String> ,

    val pitch: Long,
    val speakingRate: Long
)

@Serializable
data class Input (
    val text: String
)

@Serializable
data class Voice (
    val languageCode: String,
    val name: String
)



