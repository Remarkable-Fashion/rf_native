package com.lf.fashion.ui.addPost

import android.content.ClipData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.databinding.ItemRegistFormBinding

class ClothesRvAdapter : ListAdapter<String,ClothesRvAdapter.ClothesRvViewHolder>(ClothesCategoryDiff()) {
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
        fun bind(categories: String) {
            binding.category = categories

            //+ 버튼 클릭시 아이템 추가
            binding.addCardBtn.setOnClickListener {
                val position = this.absoluteAdapterPosition
                val newList =currentList.toMutableList()
                newList.add(position + 1, categories)
                submitList(newList)
            }
        }
    }
}
class ClothesCategoryDiff : DiffUtil.ItemCallback<String>(){
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
       return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem    }

}