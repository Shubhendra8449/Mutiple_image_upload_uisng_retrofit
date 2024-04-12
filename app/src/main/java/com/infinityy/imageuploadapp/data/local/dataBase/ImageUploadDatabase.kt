package com.infinityy.imageuploadapp.data.local.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.infinityy.imageuploadapp.data.local.dao.ImageUploadDao
import com.infinityy.imageuploadapp.data.local.entities.DBDataModel

@Database(entities = [DBDataModel::class], version = 3, exportSchema = false)
abstract class ImageUploadDatabase : RoomDatabase(){

    abstract val imageUploadDao : ImageUploadDao

}