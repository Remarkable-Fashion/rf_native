package com.lf.fashion.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.Post
import com.lf.fashion.data.response.RandomPostResponse
import com.lf.fashion.databinding.ItemSearchResultItemVerticalBinding
import com.lf.fashion.databinding.ItemSearchVerticalBinding
import com.lf.fashion.ui.home.adapter.DefaultPostDiff

class LookVerticalAdapter(private val resultCategory: String?) : ListAdapter<RandomPostResponse, LookVerticalAdapter.LookVerticalViewHolder>(DefaultPostDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookVerticalViewHolder {
        val binding = when (resultCategory) {
            "item" ->{
                ItemSearchResultItemVerticalBinding.inflate(LayoutInflater.from(parent.context),parent,false)

            }
            else -> {
                ItemSearchVerticalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            }
        }
     return LookVerticalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookVerticalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LookVerticalViewHolder(private val binding : ViewDataBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(post:RandomPostResponse){
            when(binding){
                is ItemSearchVerticalBinding ->{
                    binding.photoUrl = post.images[0].url
                    binding.executePendingBindings()
                }
                is ItemSearchResultItemVerticalBinding ->{
                    binding.included.includedClothSpace.photoUrl = post.images[0].url
                    binding.executePendingBindings()

                }
            }


        }
    }

}