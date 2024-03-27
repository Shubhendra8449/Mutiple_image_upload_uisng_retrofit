package com.infinityy.imageuploadapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.infinityy.imageuploadapp.R
import com.infinityy.imageuploadapp.adapters.ImagesListAdapter
import com.infinityy.imageuploadapp.databinding.ActivityMainBinding
import com.infinityy.imageuploadapp.domain.model.DBDataModel
import com.infinityy.imageuploadapp.domain.model.ImageUploadStatus
import com.infinityy.imageuploadapp.presentation.HomeViewModel
import com.infinityy.imageuploadapp.services.ImageUploadService
import com.infinityy.imageuploadapp.utils.AppConstants
import com.infinityy.imageuploadapp.utils.AppConstants.GALLERY_PERMISSION
import com.infinityy.imageuploadapp.utils.AppUtils.getRealPathFromURI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val vm by viewModels<HomeViewModel>()
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var service: ImageUploadService
    private var isServiceBound = false
    private var isReceiverRegistered = false

    @Inject
    lateinit var mAdapter: ImagesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initService()
        notificationPermission()
        initAdapter()
        initListener()
        observer()


    }

    /**
     * initializing bind service
     */
    private fun initService() {
        val serviceIntent = Intent(this, ImageUploadService::class.java)
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    /**
     * receiving api response from Service and change data status according to response
     */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val data = it.getStringExtra(AppConstants.BROADCAST_DATA_KEY)
                if (data == getString(R.string.failed))
                    vm.changeStatus(false, "")
                else data?.let { it1 -> vm.changeStatus(true, it1) }
            }
        }
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        // Register the BroadcastReceiver
        if (!isReceiverRegistered) {
            val filter = IntentFilter(AppConstants.BROADCAST_INTENT)
            registerReceiver(broadcastReceiver, filter, null, null)
            isReceiverRegistered = true
        }


    }

    private fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
            }
        }
    }

    /**
     * Observing this images list.
     */
    private fun observer() {
        vm.viewModelScope.launch {
            vm.event.collect {
                if (it.isEmpty())
                    mBinding.showPlaceholder.visibility = View.VISIBLE
                else
                    mBinding.showPlaceholder.visibility = View.INVISIBLE

                mAdapter.setList(ArrayList(it))
            }

        }


        vm.imageUrl.observe(this@MainActivity) {
            startImageUpload(it)
        }
    }

    private fun initListener() {
        mBinding.btnUploadImages.setOnClickListener {
            if (!hasRequiredPermission()) {
                ActivityCompat.requestPermissions(
                    this, GALLERY_PERMISSION, 0
                )
            } else openGallery()
        }
    }

    private fun initAdapter() {
        mBinding.rvImages.adapter = mAdapter
        mAdapter.onRetryClicked { pos ->
            vm.viewModelScope.launch { vm.retryUpload(mAdapter.imageData[pos].imageUrl) }
        }
    }

    private fun hasRequiredPermission(): Boolean {
        return GALLERY_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                applicationContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun openGallery() {
        val intentGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intentGallery.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        intentGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        resultLauncherGalleryPhotos.launch(intentGallery)
    }


    private val resultLauncherGalleryPhotos =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                if (data != null) {
                    val clipData = data.clipData


                    if (clipData != null) {
                        for (i in 0 until clipData.itemCount) {
                            val uri = clipData.getItemAt(i).uri
                            val filePath = getRealPathFromURI(this, uri)?.let { File(it) }
                            /**
                             * checking duplicate images.
                             */
                            if (filePath != null) {
                                var isDuplicate = false

                                vm.allData.value?.forEach { data ->
                                    if (data.imageUrl == filePath.path) {

                                        isDuplicate = true
                                    }

                                }
                                if (isDuplicate) {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.already_in_stack),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isDuplicate = false
                                }
                                /**
                                 * insert data into DB.
                                 */
                                else insertData(filePath)
                            }

                        }
                    }

                }
            }

        }

    /**
     * add data into DB.
     */
    private fun insertData(filePath: File) {
        vm.viewModelScope.launch {
            vm.insertEntry(
                DBDataModel(
                    isUpload = false,
                    status = ImageUploadStatus.NOT_START.toString(),
                    imageUrl = filePath.toString(),
                    uri = ""
                )
            )
        }
    }

    /**
     * service connection.
     */
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ImageUploadService.LocalBinder
            this@MainActivity.service = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
        unregisterReceiver(broadcastReceiver)
    }

    /**
     * passing url to service for server upload
     */
    private fun startImageUpload(imagePath: String) {
        if (isServiceBound) {
            service.uploadImage(imagePath)
        }
    }


}