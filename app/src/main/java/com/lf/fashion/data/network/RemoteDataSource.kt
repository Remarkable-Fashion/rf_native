package com.lf.fashion.data.network

import android.content.Context
import com.lf.fashion.data.common.BASE_WEB_URL
import com.lf.fashion.data.network.api.TokenRefreshApi
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RemoteDataSource @Inject constructor() {
    private fun providesHostingWebUrl() = BASE_WEB_URL

    private fun provideOkHttpClient(authenticator: Authenticator? = null): OkHttpClient {
        val client = OkHttpClient.Builder()
        authenticator?.let {
            client.authenticator(it)
        }

        return client.build()

    }
    /**
     * 최초 로그인 이후에는,
     * api를 요청할 때마다 refresh token 검사 후
     * access token의 만료시간을 연장 .. ?
     * 중간에 만료 오류 뜨면 오류 수정하기 ..
     */
    fun <Api> buildApi(
        api: Class<Api>,
        context: Context
    ): Api {
        val authenticator = TokenAuthenticator(context, buildTokenApi())
        return Retrofit.Builder()
            .baseUrl(providesHostingWebUrl())
            .client(provideOkHttpClient(authenticator))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
    private fun buildTokenApi(): TokenRefreshApi {
        return Retrofit.Builder()
            .baseUrl(providesHostingWebUrl())
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenRefreshApi::class.java)
    }
}

