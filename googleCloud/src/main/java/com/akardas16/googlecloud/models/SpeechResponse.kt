package com.akardas16.googlecloud.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeechResponse (
    val results: List<Result>,
    val totalBilledTime: String,

    @SerialName("requestId")
    val requestID: String
)

@Serializable
data class Result (
    val alternatives: List<Alternative>,
    val resultEndTime: String,
    val languageCode: String
)

@Serializable
data class Alternative (
    val transcript: String,
    val confidence: Double
)
