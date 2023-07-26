package com.lf.fashion.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ClothPost
import com.lf.fashion.data.response.LookBook
import com.lf.fashion.databinding.ItemRecommendStyleCardBinding

class LookBookRvAdapter : ListAdapter<ClothPost, LookBookRvAdapter.LookBookViewHolder>(LookBookDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookBookViewHolder {
       val binding = ItemRecommendStyleCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LookBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookBookViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    inner class LookBookViewHolder(private val binding: ItemRecommendStyleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lookBook: ClothPost,position : Int) {
            if(position <3){
                binding.cardName.text = "Best ${position+1}"
            }
            Log.d(TAG, "LookBookViewHolder - bind: ${lookBook.clothesInfo.reason}");
            binding.likes = lookBook.clothesInfo.count?.favorites
            binding.profileSpace.profile = lookBook.user
            binding.clothesSpace.cloth = lookBook.clothesInfo
            binding.likeBtn.setOnClickListener {
                binding.likeBtn.isSelected = !binding.likeBtn.isSelected
            }
        }
    }
}
class LookBookDiff : DiffUtil.ItemCallback<ClothPost>() {
    override fun areItemsTheSame(oldItem: ClothPost, newItem: ClothPost): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ClothPost, newItem: ClothPost): Boolean {
        return oldItem == newItem
    }

}