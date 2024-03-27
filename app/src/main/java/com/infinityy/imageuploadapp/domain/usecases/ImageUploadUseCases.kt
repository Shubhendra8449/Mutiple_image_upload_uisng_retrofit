package com.infinityy.imageuploadapp.domain.usecases



data class ImageUploadUseCases(
    val uploadImageApi:UploadImageApi,
    val insertEntry: InsertEntry,
    val updateEntry: UpdateEntry,
    val getAllEntry: GetAllEntry,

    )
