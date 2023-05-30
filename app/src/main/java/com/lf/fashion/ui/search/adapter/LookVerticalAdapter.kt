package com.lf.fashion.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.Post
import com.lf.fashion.databinding.ItemSearchVerticalBinding
import com.lf.fashion.ui.home.adapter.DefaultPostDiff

class LookVerticalAdapter : ListAdapter<Post, LookVerticalAdapter.LookVerticalViewHolder>(DefaultPostDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookVerticalViewHolder {
     val binding = ItemSearchVerticalBinding.inflate(LayoutInflater.from(parent.context),parent ,false)
     return LookVerticalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookVerticalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LookVerticalViewHolder(private val binding : ItemSearchVerticalBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(post:Post){
            binding.photoUrl = post.photo[0].imageUrl
            binding.executePendingBindings()


        }
    }

}