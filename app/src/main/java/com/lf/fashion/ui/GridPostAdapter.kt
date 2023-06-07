package com.lf.fashion.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.Post
import com.lf.fashion.databinding.HomeGridItemBinding
import com.lf.fashion.ui.home.adapter.DefaultPostDiff

class GridPostAdapter(private var spanCount : Int?,private val gridPhotoClickListener: GridPhotoClickListener) : ListAdapter<Post, GridPostAdapter.GridPostViewHolder>(
    DefaultPostDiff()
),
    SpanCountEditBtnListener {
    private lateinit var binding: HomeGridItemBinding
    //private var spanCount = 2
    // 메인 홈 post 는 linearLayoutManager 에서 staggerGrid 로 변경할 때 해당 GridPostAdapter 를 사용 , spanCount 2 부터 시작
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridPostViewHolder {
        binding = HomeGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return GridPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridPostViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
        //spanCount 갯수에 따라 이미지뷰 (정확히는 이미지뷰를 감싸는 ConstraintLayout 높이를 조정
        when (spanCount?:2) {
            2 -> {
                layoutParams.height = convertDPtoPX(context, 230)
             //   holder.itemView.layoutParams = layoutParams
            }
            3 -> {
                layoutParams.height = convertDPtoPX(context, 170)
             //   holder.itemView.layoutParams = layoutParams
            }
        }
        holder.itemView.layoutParams = layoutParams
        holder.bind(getItem(position))

    }

    inner class GridPostViewHolder(private val binding: HomeGridItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            //post 내부 첫번째 사진을 grid 로 노출
            binding.photoUrl = post.photo[0].imageUrl
            binding.gridImage.setOnClickListener {
                gridPhotoClickListener.gridPhotoClicked(currentList.indexOf(post))
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
interface GridPhotoClickListener{
    fun gridPhotoClicked(postIndex:Int)
}