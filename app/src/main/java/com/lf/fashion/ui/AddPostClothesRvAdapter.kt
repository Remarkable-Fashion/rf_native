package com.lf.fashion.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lf.fashion.data.model.UploadCloth
import com.lf.fashion.databinding.ItemRegistFormBinding

//PhotoStep2Fragment , RegistClothFragment 에서 사용.
class AddPostClothesRvAdapter :
    ListAdapter<UploadCloth, AddPostClothesRvAdapter.ClothesRvViewHolder>(ClothesCategoryDiff()) {

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
        fun bind(item: UploadCloth) {
            binding.clothe = item

            item.imageUrl.let {
                Glide.with(binding.root)
                    .load(item.imageUrl)
                    .into(binding.productImage)
            }

            binding.deleteBtn.setOnClickListener {
                val newList = currentList.toMutableList()
                newList.remove(item)
                submitList(newList)
            }

            binding.executePendingBindings()
        }
    }
}
class ClothesCategoryDiff : DiffUtil.ItemCallback<UploadCloth>(){
    override fun areItemsTheSame(oldItem: UploadCloth, newItem: UploadCloth): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: UploadCloth, newItem: UploadCloth): Boolean {
        return oldItem == newItem
    }

}