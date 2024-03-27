package com.infinityy.imageuploadapp.utils

import android.Manifest
import android.os.Build

object AppConstants {

    const val BASE_URL="https://www.clippr.ai/api/"
    const val IMAGE_UPLOAD_DATABASE = "image_upload_db"
    const val API_RETROFIT = "API_RETROFIT"
    const val API_HTTP_CLIENT = "API_HTTP_CLIENT"

    //Retrofit
    const val connectTimeUnit = 10L
     const val readTimeUnit = 40L
     const val writeTimeUnit = 40L

    //intent Constant
    const val BROADCAST_INTENT="intent_broad"
    const val BROADCAST_DATA_KEY="broad_key"


    val GALLERY_PERMISSION =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(

            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(

            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}