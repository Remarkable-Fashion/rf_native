package com.lf.fashion.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.databinding.ItemClothesRecommendBinding
/*

class UserInfoAdapter : ListAdapter<ClothesInfo, UserInfoAdapter.UserInfoViewHolder>(UserInfoDiff()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoViewHolder {
        val binding = ItemClothesRecommendBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserInfoViewHolder(private val binding: ItemClothesRecommendBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(clothesInfo : ClothesInfo){
                binding.includedClothSpace.cloth = clothesInfo
            }

    }
}
class UserInfoDiff : DiffUtil.ItemCallback<ClothesInfo>(){
    override fun areItemsTheSame(oldItem: ClothesInfo, newItem: ClothesInfo): Boolean {
       return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: ClothesInfo, newItem: ClothesInfo): Boolean {
        return oldItem.name == newItem.name
    }

}*/
