package com.infinityy.imageuploadapp.data.remote.api


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {

//    @Multipart
//    @POST("upload")
//    fun imageUploadApi(@Part hashMap: MultipartBody.Part): ApiResponseModel

    @Multipart
    @POST("upload")
    fun imageUploadWithService(@Part hashMap: MultipartBody.Part): Call<ResponseBody>
}