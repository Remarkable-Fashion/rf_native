package com.lf.fashion.data.network

import android.content.Context
import android.util.Log
import com.lf.fashion.TAG
import com.lf.fashion.data.common.BASE_WEB_URL
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.common.TEST_WEB_URL
import com.lf.fashion.data.network.api.TokenRefreshApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class RemoteDataSource @Inject constructor(@ApplicationContext private val context : Context) {
    private fun providesTestingWebUrl() = TEST_WEB_URL
    private fun providesHostingWebUrl() = BASE_WEB_URL
    private val userPref: PreferenceManager = PreferenceManager(context)

    private fun provideOkHttpClient(authenticator: Authenticator? = null): OkHttpClient {
        val client = OkHttpClient.Builder()
        authenticator?.let {
            client.authenticator(it)
        }
        client.interceptors().add(Interceptor { chain ->
            val original: Request = chain.request()

            // 헤더 값을 가져오기 위해 Interceptor 를 추가
            // 이 인터셉터는 loginFragment 에서 getJwt 요청시 accessToken 과 refreshToken 을 받기 위해 사용됨 !
            val requestBuilder: Request.Builder = original.newBuilder()
                .header(
                    "Authorization",
                    "auth-value"
                )
            val request: Request = requestBuilder.build()
            val response: Response = chain.proceed(request)
            val allHeaders: Headers = response.headers
            val accessJWT: String? = allHeaders["x-auth-cookie"]
            val refreshJWT: String? = allHeaders["x-auth-cookie-refresh"]


            if (accessJWT != null && refreshJWT != null) {
                runBlocking{
                    launch(Dispatchers.IO) {
                        userPref.saveAccessTokens(accessJWT,refreshJWT)
                    }
                }
            }
            response
        })

        return client.build()
    }


    /**
     * 최초 로그인 이후에는,
     * api를 요청할 때마다 refresh token 검사 후
     * access token의 만료시간을 연장 .. ?
     * 중간에 만료 오류 뜨면 오류 수정하기 ..
     *
     * RF -> 우선 token 점검하는 부분 주석해둠둠     */
    fun <Api> buildTestApi(
        api: Class<Api>,
        context: Context
    ): Api {
        //val authenticator = TokenAuthenticator(context, buildTokenApi())
        return Retrofit.Builder()
            .baseUrl(providesTestingWebUrl())
//            .client(provideOkHttpClient(authenticator))
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    fun <Api> buildApi(
        api: Class<Api>,
        context: Context
    ): Api {
        //val authenticator = TokenAuthenticator(context, buildTokenApi())
        return Retrofit.Builder()
            .baseUrl(providesHostingWebUrl())
//            .client(provideOkHttpClient(authenticator))
            .client(provideOkHttpClient())
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

