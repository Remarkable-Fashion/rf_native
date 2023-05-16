package com.lf.fashion.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.LookBook
import com.lf.fashion.databinding.ItemRecommendStyleCardBinding

class LookBookRvAdapter : ListAdapter<LookBook, LookBookRvAdapter.LookBookViewHolder>(LookBookDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookBookViewHolder {
       val binding = ItemRecommendStyleCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LookBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookBookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LookBookViewHolder(private val binding: ItemRecommendStyleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lookBook: LookBook) {
            binding.look = lookBook
            binding.profileSpace.profile = lookBook.profile
            binding.clothesSpace.cloth = lookBook.clothes

            binding.likeBtn.setOnClickListener {
                binding.likeBtn.isSelected = !binding.likeBtn.isSelected
            }
        }
    }
}
class LookBookDiff : DiffUtil.ItemCallback<LookBook>() {
    override fun areItemsTheSame(oldItem: LookBook, newItem: LookBook): Boolean {
        return oldItem.profile.userId == newItem.profile.userId
    }

    override fun areContentsTheSame(oldItem: LookBook, newItem: LookBook): Boolean {
        return oldItem.clothes.image == newItem.clothes.image
    }
}