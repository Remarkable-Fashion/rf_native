package com.lf.fashion.data.network.api.test

import com.lf.fashion.data.network.api.BaseApi
import com.lf.fashion.data.response.Post
import retrofit2.http.GET
/*
*"https://rftest-660ff-default-rtdb.firebaseio.com/"
*/
interface PhotoTestApi : BaseApi {
    @GET("posts.json")
    suspend fun getTestImages() : List<Post>
}