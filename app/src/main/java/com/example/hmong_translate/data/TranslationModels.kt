package com.example.hmong_translate.data

import com.google.gson.annotations.SerializedName

/**
 * Model phản hồi từ API khi dịch từ tiếng H'Mông sang tiếng Việt.
 *
 * Được sử dụng để parse JSON response trả về từ backend.
 *
 * Ví dụ JSON:
 * {
 *   "hmong_text": "...",
 *   "vietnamese_text": "...",
 *   "success": true,
 *   "message": null
 * }
 *
 * @property hmongText Văn bản tiếng H'Mông nhận diện từ audio
 * @property vietnameseText Văn bản tiếng Việt sau khi dịch
 * @property success Trạng thái xử lý của API
 * @property message Thông báo lỗi hoặc thông tin bổ sung từ server
 */
data class HmongToVietResponse(
    @SerializedName("hmong_text") val hmongText: String?,
    @SerializedName("vietnamese_text") val vietnameseText: String?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)

/**
 * Model phản hồi từ API khi dịch từ tiếng Việt sang tiếng H'Mông.
 *
 * Ngoài văn bản dịch, response có thể bao gồm audio được mã hóa Base64
 * để phát lại giọng nói tiếng H'Mông.
 *
 * Ví dụ JSON:
 * {
 *   "vietnamese_text": "...",
 *   "hmong_text": "...",
 *   "audio_base64": "...",
 *   "audio_format": "mp3",
 *   "sample_rate": 22050,
 *   "success": true,
 *   "message": null
 * }
 *
 * @property vietnameseText Văn bản tiếng Việt đầu vào
 * @property hmongText Văn bản tiếng H'Mông sau khi dịch
 * @property audioBase64 Audio tiếng H'Mông được mã hóa Base64 (có thể null)
 * @property audioFormat Định dạng audio (mp3, wav, ...)
 * @property sampleRate Tần số lấy mẫu audio (Hz)
 * @property success Trạng thái xử lý của API
 * @property message Thông báo lỗi hoặc thông tin bổ sung từ server
 */
data class VietToHmongResponse(
    @SerializedName("vietnamese_text") val vietnameseText: String?,
    @SerializedName("hmong_text") val hmongText: String?,
    @SerializedName("audio_base64") val audioBase64: String?,
    @SerializedName("audio_format") val audioFormat: String?,
    @SerializedName("sample_rate") val sampleRate: Int?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)
