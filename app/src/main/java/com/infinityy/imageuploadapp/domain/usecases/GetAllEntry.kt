package com.infinityy.imageuploadapp.domain.usecases

import com.infinityy.imageuploadapp.domain.model.DBDataModel
import com.infinityy.imageuploadapp.domain.repository.ImageUploadRepository
import kotlinx.coroutines.flow.Flow

class GetAllEntry(
    private val imageUploadRepository: ImageUploadRepository
) {

    operator fun invoke(): Flow<List<DBDataModel>> {
        return imageUploadRepository.getAllEntry()
    }
}