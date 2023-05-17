package com.lf.fashion.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.Post
import com.lf.fashion.databinding.HomeGridItemBinding
import com.lf.fashion.ui.convertDPtoPX

class GridPostAdapter : ListAdapter<Post, GridPostAdapter.GridPostViewHolder>(DefaultPostDiff()), SpanCountEditBtnListener {
    private lateinit var binding: HomeGridItemBinding
    private var spanCount = 2
    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridPostViewHolder {
        binding = HomeGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return GridPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridPostViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams
       //spanCount 갯수에 따라 이미지뷰 (정확히는 이미지뷰를 감싸는 ConstraintLayout 높이를 조정
        when (spanCount) {
            2 -> {
                layoutParams.height = convertDPtoPX(context , 230)
                holder.itemView.layoutParams = layoutParams
            }
            3 -> {
                layoutParams.height = convertDPtoPX(context,170)
                holder.itemView.layoutParams = layoutParams
            }
        }
        holder.bind(getItem(position))

    }

    inner class GridPostViewHolder(private val binding: HomeGridItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            //post 내부 첫번째 사진을 grid 로 노출
            binding.photoUrl = post.photo[0].imageUrl
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
