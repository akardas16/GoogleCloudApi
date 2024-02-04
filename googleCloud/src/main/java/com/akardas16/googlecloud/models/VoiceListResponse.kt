package com.akardas16.googlecloud.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceListResponse (
    val voices: List<VoiceItem>
)

@Serializable
data class VoiceItem (
    val languageCodes: List<String>,
    val name: String,
    val ssmlGender: SsmlGender,
    val naturalSampleRateHertz: Long
)

@Serializable
enum class SsmlGender(val value: String) {
    @SerialName("FEMALE") Female("FEMALE"),
    @SerialName("MALE") Male("MALE");
}
