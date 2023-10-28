package com.lf.fashion.data.repository

import com.lf.fashion.data.network.SafeApiCall
import com.lf.fashion.data.network.api.DeepLinkPostApi
import javax.inject.Inject

class DeepLinkPostRepository  @Inject constructor(
    private val deepLinkApi: DeepLinkPostApi
) : SafeApiCall {

    suspend fun getDeepLinkPost (postIt : Int , userId : Int? =null)
    = safeApiCall {
        deepLinkApi.getDeepLinkPost(postIt,userId)
    }


}