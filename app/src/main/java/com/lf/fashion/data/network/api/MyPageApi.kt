package com.lf.fashion.data.network.api

import com.lf.fashion.data.model.FollowerUserList
import com.lf.fashion.data.model.FollowingUserList
import com.lf.fashion.data.model.MsgResponse
import com.lf.fashion.data.model.MyBlockUserList
import com.lf.fashion.data.model.MyInfo
import com.lf.fashion.data.model.PostStatus
import com.lf.fashion.data.model.RandomPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface MyPageApi {
    @GET("user/me")
    suspend fun getMyPageInfo(): MyInfo

    @GET("post/me?take=21")
    suspend fun getMyPagePost(@Query("cursor") nextCursor: Int? = null): RandomPostResponse

    /*@FormUrlEncoded ("avartar")*/
    @Multipart
    @PATCH("user/me/profile")
    suspend fun updateProfileInfo(
        @Part profileImage: MultipartBody.Part?,
        @Part("sex") sex: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("weight") weight: RequestBody?,
        @Part("introduction") introduction : RequestBody?) : MsgResponse

    @GET("user/following/me")
    suspend fun getMyFollowing() : FollowingUserList

    @GET("user/follower/me")
    suspend fun getMyFollowers() : FollowerUserList

    @GET("user/block/me")
    suspend fun getMyBlockUser() : MyBlockUserList

    @DELETE("post/{id}")
    suspend fun deletePost(@Path("id") postId : Int) : MsgResponse

    @DELETE("user/follower/{id}")
    suspend fun deleteFollowerById(@Path("id") userId :Int) : MsgResponse

    @PATCH("post/{id}/status")
    suspend fun updatePostStatus(@Path("id") postId :Int ,@Body isPublic :PostStatus) :MsgResponse
}
