package com.lf.fashion.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.Post
import com.lf.fashion.databinding.HomeGridItemBinding
import com.lf.fashion.databinding.ItemSearchResultItemListBinding
import com.lf.fashion.ui.home.adapter.DefaultPostDiff

class GridPostAdapter(
    private var spanCount: Int?,
    private val gridPhotoClickListener: GridPhotoClickListener,
    private val resultCategory: String?
) : ListAdapter<Post, GridPostAdapter.GridPostViewHolder>(
    DefaultPostDiff()
), SpanCountEditBtnListener {

    //private var spanCount = 2
    // 메인 홈 post 는 linearLayoutManager 에서 staggerGrid 로 변경할 때 해당 GridPostAdapter 를 사용 , spanCount 2 부터 시작
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridPostViewHolder {
       val binding = when(resultCategory){
           "item" ->{
               ItemSearchResultItemListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
           }
           else ->{
               HomeGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
           }
       }
        context = parent.context
        return GridPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridPostViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class GridPostViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams

        fun bind(post: Post) {
            when(binding){
                is HomeGridItemBinding ->{
                    //post 내부 첫번째 사진을 grid 로 노출
                    binding.photoUrl = post.photo[0].imageUrl
                    binding.gridImage.setOnClickListener {
                        gridPhotoClickListener.gridPhotoClicked(currentList.indexOf(post))
                    }
                    //spanCount 갯수에 따라 이미지뷰 (정확히는 이미지뷰를 감싸는 ConstraintLayout 높이를 조정
                    layoutParams.height = when (spanCount ?: 2) {
                        2 -> convertDPtoPX(context, 228)
                        3 -> convertDPtoPX(context, 150)
                        else -> layoutParams.height
                    }
                }
                is ItemSearchResultItemListBinding ->{
                    binding.includedClothSpace.photoUrl = post.photo[0].imageUrl
                    layoutParams.height = when (spanCount ?: 2) {
                        2 -> convertDPtoPX(context, 300)
                        3 -> convertDPtoPX(context, 250)
                        else -> layoutParams.height

                    }
                    binding.includedClothSpace.gridImage.scaleType = ImageView.ScaleType.CENTER_CROP

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