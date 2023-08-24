package com.lf.fashion.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.databinding.ItemClothesRecommendBinding

class ClothesRvAdapter : ListAdapter<Cloth, ClothesRvAdapter.ClothesViewHolder>(ClothDiff()) {

    inner class ClothesViewHolder(private val binding : ItemClothesRecommendBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clothesInfo: Cloth){
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
class ClothDiff : DiffUtil.ItemCallback<Cloth>(){
    override fun areItemsTheSame(oldItem: Cloth, newItem: Cloth): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cloth, newItem: Cloth): Boolean {
        return oldItem == newItem
    }


}