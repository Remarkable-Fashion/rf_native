package com.lf.fashion.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.model.Posts
import com.lf.fashion.databinding.HomeGridItemBinding
import com.lf.fashion.ui.home.adapter.DefaultPostDiff

class GridPostAdapter(
    private var spanCount: Int? = null,
    private val gridPhotoClickListener: GridPhotoClickListener,
    private val scrapPage: Boolean? = null
) : ListAdapter<Posts, GridPostAdapter.GridPostViewHolder>(
    DefaultPostDiff()
), SpanCountEditBtnListener {

    //private var spanCount = 2
    // 메인 홈 post 는 linearLayoutManager 에서 staggerGrid 로 변경할 때 해당 GridPostAdapter 를 사용 , spanCount 2 부터 시작
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridPostViewHolder {
        val binding =
            HomeGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)


        context = parent.context
        return GridPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridPostViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class GridPostViewHolder(private val binding: HomeGridItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams

        fun bind(post: Posts) {
            //post 내부 첫번째 사진을 grid 로 노출
            binding.photoUrl = post.images[0].url

            binding.gridImage.setOnClickListener {
                gridPhotoClickListener.gridPhotoClicked(currentList.indexOf(post))
            }
            binding.scrapIcon.isSelected = post.isScrap ?: false

            //spanCount 갯수에 따라 이미지뷰 (정확히는 이미지뷰를 감싸는 ConstraintLayout 높이를 조정
            layoutParams.height = when (spanCount ?: 2) {
                2 -> convertDPtoPX(context, 228)
                3 -> convertDPtoPX(context, 150)
                else -> layoutParams.height
            }

            //스크랩 페이지 grid 모아보기에서 스크랩 아이콘을 숨긴다
            scrapPage?.let {
                if (scrapPage) {
                    binding.scrapIcon.visibility = View.GONE
                }
            }
            binding.executePendingBindings()
        }
    }

    override fun editSpanCountBtnClicked(newSpan: Int) {
        spanCount = newSpan
    }

}

interface SpanCountEditBtnListener {
    fun editSpanCountBtnClicked(newSpan: Int)
}

interface GridPhotoClickListener {
    fun gridPhotoClicked(postIndex: Int)
}