package com.example.hmong_translate.data

import com.google.gson.annotations.SerializedName

data class HmongToVietResponse(
    @SerializedName("hmong_text") val hmongText: String?,
    @SerializedName("vietnamese_text") val vietnameseText: String?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)

data class VietToHmongResponse(
    @SerializedName("vietnamese_text") val vietnameseText: String?,
    @SerializedName("hmong_text") val hmongText: String?,
    @SerializedName("audio_base64") val audioBase64: String?,
    @SerializedName("audio_format") val audioFormat: String?,
    @SerializedName("sample_rate") val sampleRate: Int?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
