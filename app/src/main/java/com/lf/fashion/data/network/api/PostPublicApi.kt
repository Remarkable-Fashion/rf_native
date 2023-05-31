package com.lf.fashion.data.network.api

import com.lf.fashion.data.response.PostResponse
import retrofit2.http.GET

interface PostPublicApi {
    @GET("post/public?take=15&sex=Male")
    suspend fun getPostPublic() : List<PostResponse>
}