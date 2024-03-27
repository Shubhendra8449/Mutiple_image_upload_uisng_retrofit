package com.infinityy.imageuploadapp.domain.usecases

import com.infinityy.imageuploadapp.domain.model.DBDataModel
import com.infinityy.imageuploadapp.domain.repository.ImageUploadRepository

class UpdateEntry(
    private val imageUploadRepository: ImageUploadRepository
) {

    suspend operator fun invoke(data: DBDataModel){
        imageUploadRepository.updateEntry(data)
    }
}