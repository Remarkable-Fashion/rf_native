package com.lf.fashion.data.network

import android.content.Context
import com.lf.fashion.data.network.api.JWTApi
import com.lf.fashion.data.network.api.MyPageApi
import com.lf.fashion.data.network.api.PostApi
import com.lf.fashion.data.network.api.test.ChipTestApi
import com.lf.fashion.data.network.api.test.PhotoTestApi
import com.lf.fashion.data.response.Post
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

    /* --------------------------- */

    @Singleton
    @Provides
    fun provideJWTApi(
        remoteDataSource: RemoteDataSource
    ): JWTApi {
        return remoteDataSource.buildApi(JWTApi::class.java)
    }

    @Singleton
    @Provides
    fun providePostApi(
        remoteDataSource: RemoteDataSource
    ): PostApi {
        return remoteDataSource.buildApi(PostApi::class.java)
    }

    @Singleton
    @Provides
    fun provideMyPageApi(
        remoteDataSource: RemoteDataSource
    ) : MyPageApi{
        return remoteDataSource.buildApi(MyPageApi::class.java)
    }

}