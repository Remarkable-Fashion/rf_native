package com.lf.fashion.data.network

import android.content.Context
import android.util.Log
import com.lf.fashion.TAG
import com.lf.fashion.data.common.BASE_WEB_URL
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.api.TokenRefreshApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class RemoteDataSource @Inject constructor(@ApplicationContext private val context: Context) {

    private fun providesHostingWebUrl() = BASE_WEB_URL
    private val userPref: UserDataStorePref = UserDataStorePref(context)

    private fun provideOkHttpClient(authenticator: Authenticator? = null): OkHttpClient {
        val client = OkHttpClient.Builder()
        authenticator?.let {
            client.authenticator(it)
        }


        // 헤더 값 수정을 위한 Interceptor 를 추가
        client.interceptors().add(Interceptor { chain ->
            val original: Request = chain.request()
            val isTestUrl = original.url.toString().contains("firebaseio")
            var request: Request = original

            if (!isTestUrl) {
                //userPref 에 저장된 jwt 토큰 가져와서 request Authorization Header 추가
                val requestAuthKey: Deferred<String> =
                    CoroutineScope(Dispatchers.IO).async {

                        userPref.accessToken.first() ?: ""

                    }

                val authKey = runBlocking { requestAuthKey.await() }
                //if(authKey.isNotEmpty()) {

                //testJWT 유효기간 365
                val testJWT =
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiaWF0IjoxNjg4MDA4MzQxLCJleHAiOjE3MTk1NDQzNDF9.gr5Ijgdyy_ptL29Y3CE60fZZGNJQbli_eOdrzEOHL_o"
                //  .addHeader("Authorization", "Bearer $authKey")

                val requestBuilder: Request.Builder = original.newBuilder()
                    .addHeader("Authorization", "Bearer $testJWT")
                request = requestBuilder.build()

                // }
                Log.e(TAG, "RemoteDataSource - provideOkHttpClient: ${request.url}")
            }
            //api url 에 따라 분류하여 response 인터셉트
            val response =
                with(original.url.toString()) {
                    when {
                        //로그인 시 발급된 jwt 헤더에서 추출
                        startsWith(BASE_WEB_URL + "auth/kakao") -> {
                            val response: Response = chain.proceed(request)
                            val allHeaders: Headers = response.headers
                            val accessJWT: String? = allHeaders["x-auth-cookie"]
                            val refreshJWT: String? = allHeaders["x-auth-cookie-refresh"]
                            Log.d(TAG, "RemoteDataSource - provideOkHttpClient: $accessJWT")
                            if (accessJWT != null && refreshJWT != null) {
                                runBlocking {
                                    launch(Dispatchers.IO) {
                                        userPref.saveAccessTokens(accessJWT, refreshJWT)
                                    }
                                }
                            }
                            return@with response
                        }

                       /* contains("post") -> {

                            if (this.startsWith(BASE_WEB_URL + "me")) {
                                return@with getPostResponse(chain, request)
                            }
                            return@with getPostResponse(chain, request, true)
                        }
                        startsWith(BASE_WEB_URL + "scrap") -> {
                            return@with getPostResponse(chain, request, true)
                        }*/
                        else -> {
                            return@with chain.proceed(request)
                        }
                    }
                }
            response
        })
       // body log 찍기
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(loggingInterceptor)

        return client.build()
    }

    /**
     * 최초 로그인 이후에는,
     * api를 요청할 때마다 refresh token 검사 후
     * access token의 만료시간을 연장 .. ?
     * 중간에 만료 오류 뜨면 오류 수정하기 ..
     *
     * RF -> 우선 token 점검하는 부분 주석해둠     */
    fun <Api> buildApi(
        api: Class<Api>
    ): Api {
        return Retrofit.Builder()
            .baseUrl(providesHostingWebUrl())
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

