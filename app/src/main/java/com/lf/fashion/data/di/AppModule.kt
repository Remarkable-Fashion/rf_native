package com.lf.fashion.data.di

import android.content.Context
import com.lf.fashion.data.network.RemoteDataSource
import com.lf.fashion.data.network.api.PostPublicApi
import com.lf.fashion.data.network.api.test.ChipTestApi
import com.lf.fashion.data.network.api.test.PhotoTestApi
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
        return remoteDataSource.buildTestApi(PhotoTestApi::class.java,context)
    }

    @Singleton
    @Provides
    fun provideTestChipApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): ChipTestApi {
        return remoteDataSource.buildTestApi(ChipTestApi::class.java,context)
    }

    @Singleton
    @Provides
    fun providePostPublicApi(
        remoteDataSource: RemoteDataSource,
        @ApplicationContext context: Context
    ): PostPublicApi {
        return remoteDataSource.buildApi(PostPublicApi::class.java,context)
    }
}