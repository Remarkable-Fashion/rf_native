package com.lf.fashion.ui.globalFrag.editPost

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lf.fashion.TAG
import com.lf.fashion.data.model.Cloth

import com.lf.fashion.databinding.ItemRegistFormBinding
import com.lf.fashion.ui.home.adapter.ClothDiff

class EditPostClothesRvAdapter : ListAdapter<Cloth,EditPostClothesRvAdapter.ClothesRvViewHolder>(
    ClothDiff()
) {
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
        fun bind(item: Cloth) {
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