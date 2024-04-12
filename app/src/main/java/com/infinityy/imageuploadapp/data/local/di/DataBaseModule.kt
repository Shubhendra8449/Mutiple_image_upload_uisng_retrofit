package com.infinityy.imageuploadapp.data.local.di

import android.app.Application
import androidx.room.Room
import com.infinityy.imageuploadapp.data.local.dao.ImageUploadDao
import com.infinityy.imageuploadapp.data.local.dataBase.ImageUploadDatabase
import com.infinityy.imageuploadapp.data.remote.api.ApiInterface
import com.infinityy.imageuploadapp.data.repository.ImageUploadRepositoryImpl
import com.infinityy.imageuploadapp.domain.repository.ImageUploadRepository
import com.infinityy.imageuploadapp.domain.usecases.GetAllEntry
import com.infinityy.imageuploadapp.domain.usecases.InsertEntry
import com.infinityy.imageuploadapp.domain.usecases.ImageUploadUseCases
import com.infinityy.imageuploadapp.domain.usecases.UpdateEntry
import com.infinityy.imageuploadapp.domain.usecases.UploadImageApi
import com.infinityy.imageuploadapp.utils.AppConstants.IMAGE_UPLOAD_DATABASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class) // this will live as long as the app will alive
object DataBaseModule {



    @Provides
    @Singleton
    fun provideImageUploadRepository(
        imageDao: ImageUploadDao

//        ,imageApiInterface: ApiInterface
    ): ImageUploadRepository = ImageUploadRepositoryImpl(imageDao
//        , imageApiInterface
    )

    @Provides
    @Singleton
    fun provideNewsUseCases(
        imageUploadRepository: ImageUploadRepository
    ): ImageUploadUseCases {
        return ImageUploadUseCases(
            insertEntry = InsertEntry(imageUploadRepository),
            updateEntry = UpdateEntry(imageUploadRepository),
            getAllEntry = GetAllEntry(imageUploadRepository)
//            , uploadImageApi = UploadImageApi(imageUploadRepository)
        )
    }

    @Provides
    @Singleton
    fun provideImageUploadDatabase(application: Application): ImageUploadDatabase {
        return Room.databaseBuilder(
            context = application,
            klass = ImageUploadDatabase::class.java,
            name = IMAGE_UPLOAD_DATABASE
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesImageUploadDao(imageUploadDatabase: ImageUploadDatabase): ImageUploadDao =
        imageUploadDatabase.imageUploadDao

}