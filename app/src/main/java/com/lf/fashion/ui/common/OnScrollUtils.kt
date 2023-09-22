package com.lf.fashion.ui.common

import androidx.core.widget.NestedScrollView

class OnScrollUtils(private val loadMoreCallback: () -> Unit) :
    NestedScrollView.OnScrollChangeListener {
    override fun onScrollChange(
        v: NestedScrollView,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (v.getChildAt(v.childCount - 1) != null) {
            if ((scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                scrollY > oldScrollY // Scroll Down
            ) {
                // 스크롤이 끝까지 진행되면 -> 새로운 post 를 request

                loadMoreCallback()
            }
        }
    }

}