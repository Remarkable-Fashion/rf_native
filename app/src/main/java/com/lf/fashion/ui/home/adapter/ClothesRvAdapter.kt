package com.lf.fashion.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.ClothesInfo
import com.lf.fashion.databinding.ItemClothesRecommendBinding

class ClothesRvAdapter : ListAdapter<ClothesInfo, ClothesRvAdapter.ClothesViewHolder>(ClothesDiff()) {

    inner class ClothesViewHolder(private val binding : ItemClothesRecommendBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clothesInfo: ClothesInfo){
            binding.includedClothSpace.cloth = clothesInfo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val binding =
            ItemClothesRecommendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClothesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
class ClothesDiff : DiffUtil.ItemCallback<ClothesInfo>(){
    override fun areItemsTheSame(oldItem: ClothesInfo, newItem: ClothesInfo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: ClothesInfo, newItem: ClothesInfo): Boolean {
        return oldItem.name == newItem.name    }

}