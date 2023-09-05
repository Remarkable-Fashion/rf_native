package com.lf.fashion.ui

import com.lf.fashion.data.model.Posts

interface MyBottomDialogListener {
    fun onBottomSheetDismissed(post: Posts)
    fun deleteMyPost(post:Posts)
}