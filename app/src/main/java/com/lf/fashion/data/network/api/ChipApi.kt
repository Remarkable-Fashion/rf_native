package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.ChipInfo
import retrofit2.http.GET

interface ChipApi {
    @GET("home/category/tpo")
    suspend fun getTPOChips(): List<ChipInfo>

    @GET("home/category/season")
    suspend fun getSeasonChips(): List<ChipInfo>

    @GET("home/category/style")
    suspend fun getStyleChips():List<ChipInfo>
}