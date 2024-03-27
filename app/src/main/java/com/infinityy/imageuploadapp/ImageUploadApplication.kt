package com.infinityy.imageuploadapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ImageUploadApplication : Application() {

    companion object {
        lateinit var instance: ImageUploadApplication
        lateinit var appContext: Context

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appContext = applicationContext
    }
        override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }


}