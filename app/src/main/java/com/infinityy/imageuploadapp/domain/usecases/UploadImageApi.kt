package com.infinityy.imageuploadapp.domain.usecases

import com.infinityy.imageuploadapp.data.remote.base.ApiState
import com.infinityy.imageuploadapp.domain.model.ApiRequestModel
import com.infinityy.imageuploadapp.domain.model.ApiResponseModel
import com.infinityy.imageuploadapp.domain.repository.ImageUploadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class UploadImageApi(
    private val imageUploadRepository: ImageUploadRepository
) {
    suspend operator fun invoke(data : ApiRequestModel): Flow<ApiState<ApiResponseModel>> {
        return flow {
            val response = imageUploadRepository.uploadImage(data)
            emit(ApiState.success(response))
        }.flowOn(Dispatchers.IO)
    }




}