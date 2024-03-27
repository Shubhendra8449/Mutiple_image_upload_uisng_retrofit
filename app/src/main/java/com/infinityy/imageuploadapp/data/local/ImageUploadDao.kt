package com.infinityy.imageuploadapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.infinityy.imageuploadapp.domain.model.DBDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageUploadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(data: DBDataModel)

    @Update
    suspend fun updateEntry(data: DBDataModel)

    @Query("SELECT * FROM DBDataModel")
    fun getAllEntry() : Flow<List<DBDataModel>>


}