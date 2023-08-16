package com.lf.fashion.data.response

data class FollowingUserList (
    val hasNext : Boolean,
    val followings : List<OtherUser>
)

data class FollowerUserList(
    val hasNext: Boolean,
    val followers:List<OtherUser>
)

data class MyBlockUserList(
    val hasNext: Boolean,
    val blockedUsers : List<OtherUser>
)
data class OtherUser(
    val user : UserInfo,
    val createdAt : String
)