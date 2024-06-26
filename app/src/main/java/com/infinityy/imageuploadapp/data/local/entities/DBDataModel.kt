package com.infinityy.imageuploadapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File


@Entity()
data class DBDataModel(
    var isUpload: Boolean,
    var status:String,
    @PrimaryKey val imageUrl: String,
    val uri: String,
    val uploadTime:Long?=System.currentTimeMillis()

)