package com.lf.fashion.data.di

import android.content.Context
import com.lf.fashion.data.network.RemoteDataSource
import com.lf.fashion.data.network.api.ChipTestApi
import com.lf.fashion.data.network.api.PhotoTestApi
import com.lf.fashion.ui.addPost.ImagePickerViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTestImageApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): PhotoTestApi {
        return remoteDataSource.buildApi(PhotoTestApi::class.java,context)
    }

    @Singleton
    @Provides
    fun provideTestChipApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): ChipTestApi {
        return remoteDataSource.buildApi(ChipTestApi::class.java,context)
    }

  /*  @Singleton
    @Provides
    fun provideImagePickerViewModel(
        @ApplicationContext context: Context
    ) : ImagePickerViewModel{
        return ImagePickerViewModel(context)
    }*/
}