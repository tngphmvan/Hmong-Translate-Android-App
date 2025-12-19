package com.example.hmong_translate.data

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Interface định nghĩa các API backend cho chức năng dịch giọng nói.
 *
 * ApiService được sử dụng bởi Retrofit để:
 * - Upload file audio dạng multipart/form-data
 * - Gọi các endpoint xử lý ASR + Translation (+ TTS nếu có)
 *
 * Tất cả các hàm đều là suspend function và cần được gọi trong coroutine.
 */
interface ApiService {

    /**
     * API dịch từ tiếng H'Mông sang tiếng Việt.
     *
     * Gửi file audio tiếng H'Mông lên server để:
     * 1. Nhận diện giọng nói (ASR)
     * 2. Dịch sang tiếng Việt
     *
     * Endpoint: POST /api/hmong-to-vietnamese
     *
     * @param audio File audio giọng nói tiếng H'Mông, gửi dưới dạng multipart
     * @return HmongToVietResponse chứa kết quả dịch và trạng thái xử lý
     */
    @Multipart
    @POST("api/hmong-to-vietnamese")
    suspend fun translateHmongToViet(
        @Part audio: MultipartBody.Part
    ): HmongToVietResponse

    /**
     * API dịch từ tiếng Việt sang tiếng H'Mông.
     *
     * Gửi file audio tiếng Việt lên server để:
     * 1. Nhận diện giọng nói (ASR)
     * 2. Dịch sang tiếng H'Mông
     * 3. Sinh audio giọng nói tiếng H'Mông (TTS)
     *
     * Endpoint: POST /api/vietnamese-to-hmong
     *
     * @param audio File audio giọng nói tiếng Việt, gửi dưới dạng multipart
     * @return VietToHmongResponse chứa kết quả dịch và audio Base64 (nếu thành công)
     */
    @Multipart
    @POST("api/vietnamese-to-hmong")
    suspend fun translateVietToHmong(
        @Part audio: MultipartBody.Part
    ): VietToHmongResponse
}
