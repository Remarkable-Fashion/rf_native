package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.ChipInfo
import retrofit2.http.GET

interface ChipTestApi : BaseApi {
    @GET("filter_chip.json")
    suspend fun getFilterInfo() : List<ChipInfo>
}