package com.infinityy.imageuploadapp.data.repository


import com.infinityy.imageuploadapp.data.local.ImageUploadDao
import com.infinityy.imageuploadapp.data.remote.ApiInterface
import com.infinityy.imageuploadapp.domain.model.ApiRequestModel
import com.infinityy.imageuploadapp.domain.model.ApiResponseModel
import com.infinityy.imageuploadapp.domain.model.DBDataModel
import com.infinityy.imageuploadapp.domain.repository.ImageUploadRepository
import com.infinityy.imageuploadapp.utils.AppUtils.dataClassToMultipart
import kotlinx.coroutines.flow.Flow

class ImageUploadRepositoryImpl(
    private val imageUploadDao: ImageUploadDao,
    private val imageUploadApi: ApiInterface
) : ImageUploadRepository {

    override suspend fun uploadImage(data: ApiRequestModel): ApiResponseModel {
        return imageUploadApi.imageUploadApi( dataClassToMultipart(data))
    }


    override suspend fun insertEntry(data: DBDataModel) {
        imageUploadDao.insertEntry(data)
    }

    override suspend fun updateEntry(data: DBDataModel) {
        imageUploadDao.updateEntry(data)
    }

    override fun getAllEntry(): Flow<List<DBDataModel>> {
        return imageUploadDao.getAllEntry()
    }

}