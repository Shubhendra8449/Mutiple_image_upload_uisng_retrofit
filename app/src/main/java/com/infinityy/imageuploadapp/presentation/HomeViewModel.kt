package com.infinityy.imageuploadapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infinityy.imageuploadapp.domain.model.DBDataModel
import com.infinityy.imageuploadapp.domain.model.ImageUploadStatus
import com.infinityy.imageuploadapp.domain.usecases.ImageUploadUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCases: ImageUploadUseCases
) : ViewModel() {


    private val _event: MutableSharedFlow<List<DBDataModel>> = MutableSharedFlow()
    val event = _event.asSharedFlow()
    val imageUrl = MutableLiveData<String>()
    val allData = MutableLiveData<ArrayList<DBDataModel>>()

    /**
     * initial DB calling for listing
     */
    init {
        viewModelScope.launch {
            getAllEntry()
        }
    }

    suspend fun insertEntry(data: DBDataModel) {
        useCases.insertEntry(data)
    }

    suspend fun updateEntry(data: DBDataModel) {
        useCases.updateEntry(data)
    }


    /**
     * getting list from db and update the reponse on the bases of api
     */
    fun changeStatus(apiResponse: Boolean, image: String) {
        viewModelScope.launch {

            allData.value.let {


                for (index in it?.indices!!) {
                    val dbDataModel = it[index]

                    if (dbDataModel.status == ImageUploadStatus.UPLOADING.toString()) {
                        if (apiResponse) {
                            updateEntry(
                                DBDataModel(
                                    isUpload = true,
                                    status = ImageUploadStatus.DONE.toString(),
                                    uri = image, imageUrl = dbDataModel.imageUrl
                                )
                            )
                        } else {
                            updateEntry(
                                DBDataModel(
                                    isUpload = false,
                                    status = ImageUploadStatus.FAILED.toString(),
                                    uri = "", imageUrl = dbDataModel.imageUrl
                                )
                            )
                        }
                        break  // Break out of the loop
                    }
                }
            }
        }

    }

    suspend fun getAllEntry() {
        val dataList = useCases.getAllEntry() // Assuming getAllEntry returns Flow<List<DataModel>>

        /**
         * if ->status uploading means image is uploading
         * if -> status failed means user should press retry btn.
         * if -> after first image status not start will work as queue.
         */
        dataList.collect { data ->

            for (index in data.indices) {

                val dbDataModel = data[index]

                if (dbDataModel.status == ImageUploadStatus.FAILED.toString()) {
                break
                } else if (dbDataModel.status == ImageUploadStatus.UPLOADING.toString()) {
                    val time= dbDataModel.uploadTime?.let {
                        timeDifference(
                            it
                        )
                    }

                 if (time==true)
                     updateEntry(
                         DBDataModel(
                             isUpload = false,
                             status = ImageUploadStatus.FAILED.toString(),
                             uri = "",
                             imageUrl = dbDataModel.imageUrl
                         )
                     )
                    break  // Skip to the next iteration
                } else if (dbDataModel.status == ImageUploadStatus.NOT_START.toString() && !dbDataModel.isUpload) {

                    updateEntry(
                        DBDataModel(
                            isUpload = false,
                            status = ImageUploadStatus.UPLOADING.toString(),
                            uri = "",
                            imageUrl = dbDataModel.imageUrl
                        )
                    )
                    imageUrl.value = dbDataModel.imageUrl
                    break  // Break out of the loop
                }
            }
            allData.value?.clear()
            allData.value = ArrayList(data)
            _event.emit(data)

        }

    }

    /**
     * on retry click upload single image
     */
    fun retryUpload(image: String) {
        viewModelScope.launch {

            allData.value.let {

                for (index in it?.indices!!) {
                    val dbDataModel = it[index]


                    if (dbDataModel.imageUrl == image) {
                            updateEntry(
                                DBDataModel(
                                    isUpload = false,
                                    status = ImageUploadStatus.UPLOADING.toString(),
                                    uri = "", imageUrl = image
                                )
                            )
                        imageUrl.value = dbDataModel.imageUrl
                        break
                    }

                        // Break out of the loop
                    }
                }
            }

        }

    /**
     * suppose image is uploading and will has close then it will check if uploading takes 20 sec to upload that means uploading is failed.
     */
    fun timeDifference(
        startDate: Long,

        ):Boolean {
        val currentTimeInMillis = System.currentTimeMillis()
        val timeDifferenceInMillis = currentTimeInMillis - startDate

        val timeDifferenceInSeconds = timeDifferenceInMillis / 1000 // Convert milliseconds to seconds
      return timeDifferenceInSeconds > 40
        }


}