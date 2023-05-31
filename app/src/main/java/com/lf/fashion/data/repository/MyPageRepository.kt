package com.lf.fashion.data.repository

import com.lf.fashion.data.network.api.JWTApi
import com.lf.fashion.data.response.MsgResponse
import javax.inject.Inject
import kotlin.math.log

class MyPageRepository @Inject constructor(private val jwtApi: JWTApi) {

    suspend fun getJWT(loginAccessToken : String) : MsgResponse {
        return jwtApi.getJWT(loginAccessToken)
    }
}