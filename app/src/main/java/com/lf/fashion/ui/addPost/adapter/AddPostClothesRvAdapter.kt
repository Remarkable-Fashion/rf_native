package com.lf.fashion.ui.addPost.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.RegClothes
import com.lf.fashion.databinding.ItemRegistFormBinding

class AddPostClothesRvAdapter :
    ListAdapter<RegClothes, AddPostClothesRvAdapter.ClothesRvViewHolder>(ClothesCategoryDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothesRvViewHolder {
        val binding =
            ItemRegistFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClothesRvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothesRvViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClothesRvViewHolder(private val binding: ItemRegistFormBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(regClothes: RegClothes) {
            binding.clothe = regClothes

            binding.deleteBtn.setOnClickListener {
                val newList = currentList.toMutableList()
                newList.remove(regClothes)
                submitList(newList)
            }

            binding.executePendingBindings()
        }
    }
}
class ClothesCategoryDiff : DiffUtil.ItemCallback<RegClothes>(){
    override fun areItemsTheSame(oldItem: RegClothes, newItem: RegClothes): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: RegClothes, newItem: RegClothes): Boolean {
        return oldItem.name == newItem.name
    }

}