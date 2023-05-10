package com.lf.fashion.data.repository

import com.lf.fashion.data.network.api.ChipTestApi
import com.lf.fashion.data.network.api.PhotoTestApi
import com.lf.fashion.data.response.ChipInfo
import com.lf.fashion.data.response.Post
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val photoTestApi : PhotoTestApi,
    private val chipTestApi: ChipTestApi
) : BaseRepository(photoTestApi) {

    suspend fun getTestPostList() : List<Post> {
        return photoTestApi.getTestImages()
    }

    suspend fun getChipInfo():List<ChipInfo>{
        return chipTestApi.getFilterInfo()
    }
}