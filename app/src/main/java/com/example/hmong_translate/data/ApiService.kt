package com.example.hmong_translate.data

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("api/hmong-to-vietnamese")
    suspend fun translateHmongToViet(
        @Part audio: MultipartBody.Part
    ): HmongToVietResponse

    @Multipart
    @POST("api/vietnamese-to-hmong")
    suspend fun translateVietToHmong(
        @Part audio: MultipartBody.Part
    ): VietToHmongResponse
}
