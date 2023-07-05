package com.lf.fashion.data.network

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.kakao.sdk.common.util.KakaoJson.toJson
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
import kotlinx.coroutines.flow.last
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import kotlin.math.log


class RemoteDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    private fun providesTestingWebUrl() = TEST_WEB_URL
    private fun providesHostingWebUrl() = BASE_WEB_URL
    private val userPref: PreferenceManager = PreferenceManager(context)

    private fun provideOkHttpClient(authenticator: Authenticator? = null): OkHttpClient {
        val client = OkHttpClient.Builder()
        authenticator?.let {
            client.authenticator(it)
        }


        // 헤더 값을 가져오기 위해 Interceptor 를 추가
        // requestBuilder 는 요청시 header 에 jwt 토큰을 담는 역할
        // response header 탐색은 loginFragment 에서 getJwt 요청시 accessToken 과 refreshToken 을 받기 위해 사용됨 !
        client.interceptors().add(Interceptor { chain ->
            val original: Request = chain.request()

            val requestAuthKey: Deferred<String> =
                CoroutineScope(Dispatchers.IO).async {

                    userPref.accessToken.first() ?: ""

                }

            val authKey = runBlocking { requestAuthKey.await() }
            Log.d(TAG, "RemoteDataSource - provideOkHttpClient: ${authKey}");
            //if(authKey.isNotEmpty()) {
                val requestBuilder: Request.Builder = original.newBuilder()
                    .addHeader("Authorization", "Bearer $authKey")
                val request: Request = requestBuilder.build()
                Log.d(
                    TAG,
                    "RemoteDataSource - provideOkHttpClient REQUEST HEADER !! : ${request.headers}"
                );
           // }
            val response =
                with(original.url.toString()) {
                    when {
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

    private fun getPostResponseOnly(
        chain: Interceptor.Chain,
        original: Request
    ): Response {

        val response = chain.proceed(original)
        val responseBody = response.body?.string()
        val jsonObject = JSONObject(responseBody)
        return try {
            val postsArray = jsonObject.getJSONArray("posts").toString()

            response.newBuilder()
                .body(postsArray.toResponseBody(response.body?.contentType()))
                .build()

        } catch (e: Exception) {

            val msg = jsonObject.getString("msg")
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
     * RF -> 우선 token 점검하는 부분 주석해둠둠     */
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

