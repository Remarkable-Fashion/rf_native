package com.lf.fashion.ui.home

import com.lf.fashion.data.response.ClothPost

interface ClothLikeClickListener {
    fun clothLikeBtnClicked(likeState : Boolean,clothes :ClothPost)

}