package com.lf.fashion.data.network.api

import retrofit2.http.GET

interface PhotoTestApi {
    @GET
    fun getTestImages()
}