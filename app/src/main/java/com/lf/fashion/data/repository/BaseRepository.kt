package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.BaseApi

abstract class BaseRepository(private val api : BaseApi) : SafeApiCall {

 /*   suspend fun logout() = safeApiCall {
        api.logout()
    }*/
}