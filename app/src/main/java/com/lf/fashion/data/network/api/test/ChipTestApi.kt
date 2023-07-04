package com.lf.fashion.data.network.api.test

import com.lf.fashion.data.network.api.BaseApi
import com.lf.fashion.data.response.ChipInfo
import com.lf.fashion.data.response.LookBook
import com.lf.fashion.data.response.TestUserInfo
import retrofit2.http.GET

interface ChipTestApi : BaseApi {
    @GET("filter_chip.json")
    suspend fun getFilterInfo() : List<ChipInfo>

    @GET("user_clothes_info.json")
    suspend fun getUserInfoAndStyle() : TestUserInfo

    @GET("look_book.json")
    suspend fun getLookBook():List<LookBook>
}