package com.channel.core.upload.di

import com.channel.core.upload.data.UploadService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object UploadNetworkModule {

    @Provides
    @Singleton
    fun provideUploadService(retrofit: Retrofit): UploadService = retrofit.create(UploadService::class.java)
}
