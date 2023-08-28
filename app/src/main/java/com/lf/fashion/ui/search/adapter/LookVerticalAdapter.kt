package com.lf.fashion.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.SearchItemResult
import com.lf.fashion.databinding.ItemSearchResultItemVerticalBinding
import com.lf.fashion.databinding.ItemSearchVerticalBinding
import com.lf.fashion.ui.home.adapter.DefaultPostDiff

class LookVerticalAdapter() :
    ListAdapter<Posts, LookVerticalAdapter.LookVerticalViewHolder>(DefaultPostDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookVerticalViewHolder {
        val binding =
            ItemSearchVerticalBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return LookVerticalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookVerticalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LookVerticalViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Posts) {
            when (binding) {
                is ItemSearchVerticalBinding -> {
                    binding.photoUrl = post.images[0].url
                    binding.executePendingBindings()
                }

                is ItemSearchResultItemVerticalBinding -> {
                    binding.included.includedClothSpace.photoUrl = post.images[0].url
                    binding.executePendingBindings()

                }
            }


        }
    }

}