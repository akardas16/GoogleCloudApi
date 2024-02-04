package com.akardas16.googlecloud.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AudioResponse (
    val audioContent: String,
    val audioConfig: AudioConfigResult
)

@Serializable
data class AudioConfigResult (
    val audioEncoding: String,
    val speakingRate: Long,
    val pitch: Long,

    @SerialName("volumeGainDb")
    val volumeGainDB: Long,

    val sampleRateHertz: Long,

    @SerialName("effectsProfileId")
    val effectsProfileID: List<String>
)
