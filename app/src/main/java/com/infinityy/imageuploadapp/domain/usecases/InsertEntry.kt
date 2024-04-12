package com.infinityy.imageuploadapp.domain.usecases

import com.infinityy.imageuploadapp.data.local.entities.DBDataModel
import com.infinityy.imageuploadapp.domain.repository.ImageUploadRepository

class InsertEntry(
    private val imageUploadRepository: ImageUploadRepository
) {

    suspend operator fun invoke(data: DBDataModel){
        imageUploadRepository.insertEntry(data)
    }
}