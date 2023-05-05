package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.Post
import com.lf.fashion.data.response.PostTestResponse
import retrofit2.http.GET

interface PhotoTestApi :BaseApi{
    @GET("post_list.json")
    suspend fun getTestImages() : List<Post>
}