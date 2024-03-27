package com.infinityy.imageuploadapp.di

import com.infinityy.imageuploadapp.data.remote.ApiInterface
import com.infinityy.imageuploadapp.utils.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiRetrofit {
    fun provider() : ApiInterface{
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
            .create(ApiInterface::class.java)
    }
    // Creating OkHttpclient Object
    private fun getClient(): OkHttpClient {

        return OkHttpClient.Builder()
            .connectTimeout(AppConstants.connectTimeUnit, TimeUnit.SECONDS)
            .readTimeout(AppConstants.readTimeUnit, TimeUnit.SECONDS)
            .writeTimeout(AppConstants.writeTimeUnit, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build()


    }
}