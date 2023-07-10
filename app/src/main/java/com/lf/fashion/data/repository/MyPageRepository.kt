package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.JWTApi
import com.lf.fashion.data.network.api.MyPageApi
import com.lf.fashion.data.response.MsgResponse
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.data.response.RandomPostResponse
import javax.inject.Inject
import kotlin.math.log

class MyPageRepository @Inject constructor(private val jwtApi: JWTApi,private val myPageApi: MyPageApi) :SafeApiCall {

    suspend fun getJWT(loginAccessToken : String) = safeApiCall{
        jwtApi.getJWT(loginAccessToken)
    }

    suspend fun getMyInfo():MyInfo{
        return myPageApi.getMyPageInfo()
    }

    suspend fun getMyPost() : RandomPostResponse{
        return myPageApi.getMyPagePost()
    }
}