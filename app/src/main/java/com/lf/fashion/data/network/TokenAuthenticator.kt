package com.lf.fashion.data.network

import android.content.Context
import android.util.Log
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.api.TokenRefreshApi
import com.lf.fashion.data.repository.BaseRepository
import com.lf.fashion.data.response.TokenResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/*
* 자체 회원가입이 추가되고 메인으로 사용될 경우 연결될 로직.. (현재는 사용되지않는다)
* */
class TokenAuthenticator @Inject constructor(
    context: Context,
    private val tokenApi: TokenRefreshApi
) : Authenticator, BaseRepository(tokenApi) {

    private val appContext = context.applicationContext
    private val userPreferences = PreferenceManager(appContext)

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            when (val tokenResponse = getUpdatedToken()) {
                is Resource.Success -> {

                    userPreferences.saveAccessTokens(
                        tokenResponse.value.access_token!!,
                        tokenResponse.value.refresh_token!!
                    )
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${tokenResponse.value.access_token}")
                        .build()
                }
                else -> {
                    null

                }
            }
        }
    }

    private suspend fun getUpdatedToken(): Resource<TokenResponse> {
        val expiredAccessToken = userPreferences.accessToken.first()
        val refreshToken = userPreferences.refreshToken.first()
        return safeApiCall { tokenApi.refreshAccessToken(expiredAccessToken,refreshToken) }
    }

}
