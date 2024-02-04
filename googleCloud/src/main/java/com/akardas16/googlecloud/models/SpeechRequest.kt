package com.akardas16.googlecloud.models

import kotlinx.serialization.Serializable

@Serializable
data class SpeechRequest (
    val audio: Audio,
    val config: Config
)

@Serializable
data class Audio (
    val content: String
)

@Serializable
data class Config (
    val languageCode: String,
    val encoding: String,
    val sampleRateHertz: Long
)
