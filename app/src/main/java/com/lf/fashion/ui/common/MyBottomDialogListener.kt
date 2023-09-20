package com.lf.fashion.ui.common

import com.lf.fashion.data.model.Posts

interface MyBottomDialogListener {
    fun onBottomSheetDismissed(post: Posts)
    fun deleteMyPost(post:Posts)
    fun changePostPublicStatus(post:Posts)

    fun editPost(post:Posts)
}