package com.lf.fashion.data.network

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.lf.fashion.TAG
import com.lf.fashion.data.common.BASE_WEB_URL
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.common.TEST_WEB_URL
import com.lf.fashion.data.network.api.TokenRefreshApi
import com.lf.fashion.data.response.Count
import com.lf.fashion.data.response.RandomPostResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class RemoteDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    private fun providesTestingWebUrl() = TEST_WEB_URL
    private fun providesHostingWebUrl() = BASE_WEB_URL
    private val userPref: PreferenceManager = PreferenceManager(context)

    private fun provideOkHttpClient(authenticator: Authenticator? = null): OkHttpClient {
        val client = OkHttpClient.Builder()
        authenticator?.let {
            client.authenticator(it)
        }


        // 헤더 값 수정을 위한 Interceptor 를 추가
        client.interceptors().add(Interceptor { chain ->
            val original: Request = chain.request()

            //userPref 에 저장된 jwt 토큰 가져와서 request Authorization Header 추가
            val requestAuthKey: Deferred<String> =
                CoroutineScope(Dispatchers.IO).async {

                    userPref.accessToken.first() ?: ""

                }

            val authKey = runBlocking { requestAuthKey.await() }
            Log.d(TAG, "RemoteDataSource - provideOkHttpClient: ${authKey}");
            //if(authKey.isNotEmpty()) {

            //testJWT 유효기간 365
            val testJWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiaWF0IjoxNjg4MDA4MzQxLCJleHAiOjE3MTk1NDQzNDF9.gr5Ijgdyy_ptL29Y3CE60fZZGNJQbli_eOdrzEOHL_o"
            //  .addHeader("Authorization", "Bearer $authKey")

                val requestBuilder: Request.Builder = original.newBuilder()
                    .addHeader("Authorization","Bearer $testJWT")
            val request: Request = requestBuilder.build()

           // }

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

                        startsWith(BASE_WEB_URL + "post") -> {
                            return@with getPostResponseOnly(chain, request)
                        }
                        startsWith(BASE_WEB_URL + "scrap") -> {
                            return@with getPostResponseOnly(chain, request)
                        }
                        else -> {
                            return@with chain.proceed(request)
                        }
                    }
                }
            response
        })

        return client.build()
    }

    //response json 에서 posts 만 꺼내오기
    private fun getPostResponseOnly(
        chain: Interceptor.Chain,
        original: Request
    ): Response {

        val response = chain.proceed(original)
        val responseBody = response.body?.string()
        val jsonObject = responseBody?.let { JSONObject(it) }
        return try {
            val postsArray = jsonObject?.getJSONArray("posts").toString()

            response.newBuilder()
                .body(postsArray.toResponseBody(response.body?.contentType()))
                .build()

        } catch (e: Exception) {
            //response 객체 타입을 RandomPostResponse 로 고정해서, 새객체 생성후 msg 만 추가 -> 나중에 CallBack Model 재구성해도 된당
            val msg = jsonObject?.getString("msg")
            val errorResponse = RandomPostResponse(msg = msg, id = 0, isScrap = false, createdAt = "", images = emptyList(), user = null, count = Count())
            val errorResponseBody = Gson().toJson(errorResponse)
            Log.d(TAG, "RemoteDataSource - scrap: $errorResponseBody");

            response.newBuilder().body(errorResponseBody.toResponseBody(response.body?.contentType())).build()
        }
    }


    /**
     * 최초 로그인 이후에는,
     * api를 요청할 때마다 refresh token 검사 후
     * access token의 만료시간을 연장 .. ?
     * 중간에 만료 오류 뜨면 오류 수정하기 ..
     *
     * RF -> 우선 token 점검하는 부분 주석해둠     */
    fun <Api> buildTestApi(
        api: Class<Api>,
        context: Context
    ): Api {
        // val authenticator = TokenAuthenticator(context, buildTokenApi())
        return Retrofit.Builder()
            .baseUrl(providesTestingWebUrl())
//            .client(provideOkHttpClient(authenticator))
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    fun <Api> buildApi(
        api: Class<Api>
    ): Api {
        // val authenticator = TokenAuthenticator(context, buildTokenApi())
        return Retrofit.Builder()
            .baseUrl(providesHostingWebUrl())
            // .client(provideOkHttpClient(authenticator))
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

