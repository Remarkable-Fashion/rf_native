package com.lf.fashion.ui.home

import com.lf.fashion.data.response.Posts

interface VerticalViewPagerClickListener {
    fun likeBtnClicked(likeState: Boolean, post: Posts)
    fun scrapBtnClicked(scrapState : Boolean,post:Posts)
    fun shareBtnClicked(post : Posts)
    fun kebabBtnClicked(post : Posts)
    fun photoZipBtnClicked(post : Posts)
    fun infoBtnClicked(postId:Int)
    fun profileSpaceClicked(userId : Int)
}