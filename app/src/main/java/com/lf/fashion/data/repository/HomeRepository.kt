package com.lf.fashion.data.repository

import com.lf.fashion.data.network.api.PhotoTestApi
import com.lf.fashion.data.response.Post
import com.lf.fashion.data.response.PostTestResponse
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiService : PhotoTestApi
) : BaseRepository(apiService) {

    fun getTestPostList() : List<Post> {
        return apiService.getTestImages()
    }
}