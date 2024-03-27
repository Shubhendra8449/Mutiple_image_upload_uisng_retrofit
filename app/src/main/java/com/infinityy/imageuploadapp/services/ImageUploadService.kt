package com.infinityy.imageuploadapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.infinityy.imageuploadapp.R
import com.infinityy.imageuploadapp.di.ApiRetrofit
import com.infinityy.imageuploadapp.domain.model.ApiRequestModel
import com.infinityy.imageuploadapp.domain.model.ApiResponseModel
import com.infinityy.imageuploadapp.utils.AppConstants
import com.infinityy.imageuploadapp.utils.AppUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ImageUploadService : Service() {


    private val notificationChannel = "Image UploadChannel"
    private val clearNotificationDelay = 123456
    private val notificationId = 12345
    private val binder = LocalBinder()


    inner class LocalBinder : Binder() {
        fun getService(): ImageUploadService = this@ImageUploadService

    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }


    fun uploadImage(imagePath: String) {
        startForeground(notificationId, getNotification())

        try {
            // Call function to upload image to server
            val file = File(imagePath)
            val data = ApiRequestModel(image = file)

            /**
             * converting dataClass into Multipart.
             */
            val call = ApiRetrofit.provider().imageUploadWithService(AppUtils.dataClassToMultipart(data))

            //Api call using retrofit.
            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    updateNotification(getString(R.string.failed))
                    sendDataToActivity(getString(R.string.failed))
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {


                    val mData = Gson().fromJson(
                        response.body()?.string(),
                        ApiResponseModel::class.java
                    )

                    //Passing response on activity using broadcast receiver.
                    sendDataToActivity(mData.image)
                    updateNotification(getString(R.string.uploaded))

                }
            })

        } catch (e: Exception) {
            sendDataToActivity(getString(R.string.failed))
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        stopSelf()
    }

    private fun getNotification(): Notification {
        createNotificationChannel()

        return NotificationCompat.Builder(this, notificationChannel)
            .setContentTitle("Image Upload")
            .setContentText("Uploading...")
            .setSmallIcon(R.drawable.ic_launcher_background) // Set appropriate icon
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannel,
                "Image Upload Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(message: String) {
        // Get notification manager
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Build notification
        val notificationBuilder = NotificationCompat.Builder(this, notificationChannel)
            .setContentTitle("Image Upload")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_background)

        // If upload is successful, clear notification after a delay

            Handler(Looper.getMainLooper()).postDelayed({
                notificationManager.cancel(notificationId)
            }, clearNotificationDelay.toLong())


        // Notify
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun sendDataToActivity(data: String) {
        val intent = Intent(AppConstants.BROADCAST_INTENT)
        intent.putExtra(AppConstants.BROADCAST_DATA_KEY, data)
        sendBroadcast(intent)
    }
}
