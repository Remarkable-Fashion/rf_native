package com.lf.fashion.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.lf.fashion.TAG
import com.lf.fashion.data.response.Post
import com.lf.fashion.databinding.HomeVerticalItemBinding
import com.lf.fashion.ui.home.PhotoClickListener

class DefaultPostAdapter(private val photoClickListener: PhotoClickListener) :
    ListAdapter<Post, DefaultPostAdapter.DefaultPostViewHolder>(DefaultPostDiff()) {

    private lateinit var binding: HomeVerticalItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultPostViewHolder {
        binding =
            HomeVerticalItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DefaultPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DefaultPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DefaultPostViewHolder(private val binding: HomeVerticalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val nestedAdapter = PhotoHorizontalAdapter(photoClickListener)
        init {
            with(binding.horizontalViewPager){
                adapter = nestedAdapter

                TabLayoutMediator(
                    binding.viewpagerIndicator,
                    this
                ){_,_ ->}.attach()
            }
        }
        fun bind(post: Post) {
            binding.post = post
            //임시적으로 이미지만 처리하기에 여기서 적용함
            binding.likeBtn.setOnClickListener {
                it.isSelected = !it.isSelected
            }
            nestedAdapter.submitList(post.photo)
        }
    }
}

class DefaultPostDiff : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

}