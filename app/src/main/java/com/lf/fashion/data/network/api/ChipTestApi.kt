package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.ChipInfo
import com.lf.fashion.data.response.UserInfo
import retrofit2.http.GET

interface ChipTestApi : BaseApi {
    @GET("filter_chip.json")
    suspend fun getFilterInfo() : List<ChipInfo>

    @GET("user_clothes_info.json")
    suspend fun getUserInfoAndStyle() : UserInfo
}