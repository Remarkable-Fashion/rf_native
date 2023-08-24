package com.lf.fashion.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.Cloth
import com.lf.fashion.databinding.ItemClothesListBinding
import com.lf.fashion.ui.home.adapter.ClothDiff

class ItemRvAdapter : ListAdapter<Cloth, ItemRvAdapter.ClothesViewHolder>(ClothDiff()) {

    inner class ClothesViewHolder(private val binding : ItemClothesListBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(clothesInfo: Cloth){
            binding.includedClothSpace.cloth = clothesInfo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesViewHolder {
        val binding =
            ItemClothesListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClothesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
/*class ClothesDiff : DiffUtil.ItemCallback<ClothesInfo>(){
    override fun areItemsTheSame(oldItem: ClothesInfo, newItem: ClothesInfo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: ClothesInfo, newItem: ClothesInfo): Boolean {
        return oldItem.name == newItem.name    }

}*/
