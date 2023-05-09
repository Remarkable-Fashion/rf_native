package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.Post
import com.lf.fashion.data.response.PostTestResponse
import retrofit2.http.GET
/*
*"https://rftest-660ff-default-rtdb.firebaseio.com/"
*/
interface PhotoTestApi :BaseApi{
    @GET("posts.json")
    suspend fun getTestImages() : List<Post>
}