package com.lf.fashion.ui.home

import com.lf.fashion.data.response.Posts

interface VerticalViewPagerClickListener {
    fun likeBtnClicked(likeState: Boolean, post: Posts)
    fun scrapBtnClicked(scrapState : Boolean,post:Posts)
    fun shareBtnClicked(post : Posts)
    fun photoZipBtnClicked()
    fun infoBtnClicked(postId:Int)
}