package com.lf.fashion.ui.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpaceItemDecoration(private val spanCount: Int, private val space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount + 1      // 1부터 시작

        /** 첫번째 행(row-1) 이후부터 있는 아이템에만 상단에 [space] 만큼의 여백을 추가한다. 즉, 첫번째 행에 있는 아이템에는 상단에 여백을 주지 않는다.*/
        if (position >= spanCount){
            outRect.top = space
        }
        /** 첫번째 열이 아닌(None Column-1) 아이템들만 좌측에 [space] 만큼의 여백을 추가한다. 즉, 첫번째 열에 있는 아이템에는 좌측에 여백을 주지 않는다. */
        if (column != 1){
            outRect.left = space
        }

    }

}