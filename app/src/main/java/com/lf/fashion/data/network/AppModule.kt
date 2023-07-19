package com.lf.fashion.data.network

import android.content.Context
import com.lf.fashion.data.network.api.*
import com.lf.fashion.data.network.api.test.ChipTestApi
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
    ): MainHomeApi {
        return remoteDataSource.buildApi(MainHomeApi::class.java)
    }

    @Singleton
    @Provides
    fun provideMyPageApi(
        remoteDataSource: RemoteDataSource
    ) : MyPageApi{
        return remoteDataSource.buildApi(MyPageApi::class.java)
    }

    @Singleton
    @Provides
    fun provideScrapApi(
        remoteDataSource: RemoteDataSource
    ) : ScrapApi{
        return remoteDataSource.buildApi(ScrapApi::class.java)
    }

    @Singleton
    @Provides
    fun provideSearchApi(
        remoteDataSource: RemoteDataSource
    ) : SearchApi {
        return remoteDataSource.buildApi(SearchApi::class.java)
    }

}