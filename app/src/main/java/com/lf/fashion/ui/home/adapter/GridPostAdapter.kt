package com.lf.fashion.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.Post
import com.lf.fashion.databinding.HomeGridItemBinding

class GridPostAdapter : ListAdapter<Post, GridPostAdapter.GridPostViewHolder>(DefaultPostDiff()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridPostViewHolder {
        val binding = HomeGridItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GridPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class GridPostViewHolder(private val binding:HomeGridItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(post : Post){
            //첫번째 사진을 grid 로 노출
            binding.photoUrl = post.photo[0].imageUrl
        }
    }

}
