package com.infinityy.imageuploadapp.domain.repository


import com.infinityy.imageuploadapp.domain.model.ApiRequestModel
import com.infinityy.imageuploadapp.domain.model.ApiResponseModel
import com.infinityy.imageuploadapp.data.local.entities.DBDataModel
import kotlinx.coroutines.flow.Flow

interface ImageUploadRepository {


//    suspend fun uploadImage(data: ApiRequestModel): ApiResponseModel
    suspend fun insertEntry(data: DBDataModel)

    suspend fun updateEntry(data: DBDataModel)

    fun getAllEntry(): Flow<List<DBDataModel>>

}