package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.JWTApi
import com.lf.fashion.data.response.MsgResponse
import javax.inject.Inject
import kotlin.math.log

class MyPageRepository @Inject constructor(private val jwtApi: JWTApi) :SafeApiCall {

    suspend fun getJWT(loginAccessToken : String) = safeApiCall{
        jwtApi.getJWT(loginAccessToken)
    }
}