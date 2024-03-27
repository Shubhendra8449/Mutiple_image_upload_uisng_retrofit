package com.infinityy.imageuploadapp.di

import android.app.Application
import androidx.room.Room
import com.infinityy.imageuploadapp.data.local.ImageUploadDao
import com.infinityy.imageuploadapp.data.local.ImageUploadDatabase
import com.infinityy.imageuploadapp.data.remote.ApiInterface
import com.infinityy.imageuploadapp.data.repository.ImageUploadRepositoryImpl
import com.infinityy.imageuploadapp.domain.repository.ImageUploadRepository
import com.infinityy.imageuploadapp.domain.usecases.GetAllEntry
import com.infinityy.imageuploadapp.domain.usecases.InsertEntry
import com.infinityy.imageuploadapp.domain.usecases.ImageUploadUseCases
import com.infinityy.imageuploadapp.domain.usecases.UpdateEntry
import com.infinityy.imageuploadapp.domain.usecases.UploadImageApi
import com.infinityy.imageuploadapp.utils.AppConstants
import com.infinityy.imageuploadapp.utils.AppConstants.API_HTTP_CLIENT
import com.infinityy.imageuploadapp.utils.AppConstants.BASE_URL
import com.infinityy.imageuploadapp.utils.AppConstants.IMAGE_UPLOAD_DATABASE
import com.infinityy.imageuploadapp.utils.AppConstants.connectTimeUnit
import com.infinityy.imageuploadapp.utils.AppConstants.readTimeUnit
import com.infinityy.imageuploadapp.utils.AppConstants.writeTimeUnit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class) // this will live as long as the app will alive
object AppModule {


    /**
     * The method returns the Cache object
     * */
    @Provides
    @Singleton
    internal fun provideCache(application: Application): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        val httpCacheDirectory = File(application.cacheDir, "http-cache")
        return Cache(httpCacheDirectory, cacheSize)
    }

    /**
     * This methods returns the Okhttp object
     */
    @Provides
    @Singleton
    @Named(API_HTTP_CLIENT)
    internal fun provideOkHttpClientForApi(
        cache: Cache
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(connectTimeUnit, TimeUnit.SECONDS)
            .readTimeout(readTimeUnit, TimeUnit.SECONDS)
            .writeTimeout(writeTimeUnit, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))


        return httpClient.build()

    }

    @Provides
    @Singleton
    fun providesImageUploadApi(
        @Named(API_HTTP_CLIENT) okHttpClient: OkHttpClient

    ): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // to convert json to kotlin object
            .client(okHttpClient).build()
            .create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideImageUploadRepository(
        imageDao: ImageUploadDao,
        imageApiInterface: ApiInterface
    ): ImageUploadRepository = ImageUploadRepositoryImpl(imageDao, imageApiInterface)

    @Provides
    @Singleton
    fun provideNewsUseCases(
        imageUploadRepository: ImageUploadRepository
    ): ImageUploadUseCases {
        return ImageUploadUseCases(
            insertEntry = InsertEntry(imageUploadRepository),
            updateEntry = UpdateEntry(imageUploadRepository),
            getAllEntry = GetAllEntry(imageUploadRepository),
            uploadImageApi = UploadImageApi(imageUploadRepository)
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