package com.mani.wirup

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("summary") // Adjust the endpoint as needed
    fun uploadAudio(@Part file: MultipartBody.Part): Call<UploadResponse>
}