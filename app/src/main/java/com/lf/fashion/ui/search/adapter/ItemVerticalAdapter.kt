package com.lf.fashion.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.data.model.Posts
import com.lf.fashion.data.model.SearchItemResult
import com.lf.fashion.databinding.ItemSearchResultItemVerticalBinding
import com.lf.fashion.databinding.ItemSearchVerticalBinding
import com.lf.fashion.ui.home.adapter.DefaultPostDiff

class ItemVerticalAdapter : ListAdapter<Cloth, ItemVerticalAdapter.LookVerticalViewHolder>(object : DiffUtil.ItemCallback<Cloth>(){
    override fun areItemsTheSame(oldItem: Cloth, newItem: Cloth): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cloth, newItem: Cloth): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookVerticalViewHolder {
        val binding= ItemSearchResultItemVerticalBinding.inflate(LayoutInflater.from(parent.context),parent,false)
     return LookVerticalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookVerticalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LookVerticalViewHolder(private val binding : ViewDataBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item : Cloth){
            when(binding){
                is ItemSearchVerticalBinding ->{
                    binding.photoUrl = item.imageUrl
                    binding.executePendingBindings()
                }
                is ItemSearchResultItemVerticalBinding ->{
                    binding.included.includedClothSpace.photoUrl = item.imageUrl
                    binding.executePendingBindings()

                }
            }


        }
    }

}