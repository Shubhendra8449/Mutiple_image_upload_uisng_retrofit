package com.infinityy.imageuploadapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.infinityy.imageuploadapp.domain.model.DBDataModel

@Database(entities = [DBDataModel::class], version = 2, exportSchema = false)
abstract class ImageUploadDatabase : RoomDatabase(){

    abstract val imageUploadDao : ImageUploadDao

}