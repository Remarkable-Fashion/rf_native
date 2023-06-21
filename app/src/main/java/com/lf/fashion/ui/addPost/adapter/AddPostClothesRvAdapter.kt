package com.lf.fashion.ui.addPost.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.RegClothes
import com.lf.fashion.databinding.ItemRegistFormBinding

class AddPostClothesRvAdapter :
    ListAdapter<RegClothes, AddPostClothesRvAdapter.ClothesRvViewHolder>(ClothesCategoryDiff()) {

    var addedItemPosition :Int?  = null
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

/*            //+ 버튼 클릭시 아이템 추가
            binding.addCardBtn.setOnClickListener {
                val position = this.absoluteAdapterPosition
                val newList = currentList.toMutableList()
                newList.add(position + 1, categories)
                submitList(newList)
                addedItemPosition = position+1
            }

            // 추가된 뷰에만 - 버튼 추가
            if(addedItemPosition==this.absoluteAdapterPosition){
                binding.deleteCardBtn.visibility = View.VISIBLE
            }

            binding.deleteCardBtn.setOnClickListener {
                val position = this.absoluteAdapterPosition
                val newList = currentList.toMutableList()
                newList.removeAt(position)
                submitList(newList)
            }*/
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