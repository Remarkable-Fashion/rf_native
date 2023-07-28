package com.lf.fashion.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.response.ClothPost
import com.lf.fashion.databinding.ItemRecommendStyleCardBinding
import com.lf.fashion.ui.PrefCheckService
import dagger.hilt.android.qualifiers.ApplicationContext

class LookBookRvAdapter : ListAdapter<ClothPost, LookBookRvAdapter.LookBookViewHolder>(LookBookDiff()) {
    private lateinit var userPref: PreferenceManager
    private lateinit var prefCheckService: PrefCheckService

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookBookViewHolder {
       val binding = ItemRecommendStyleCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        userPref = PreferenceManager(parent.context.applicationContext)
        prefCheckService = PrefCheckService(userPref)

        return LookBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookBookViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    inner class LookBookViewHolder(private val binding: ItemRecommendStyleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lookBook: ClothPost,position : Int) {

            binding.profileSpace.followBtn.isVisible = prefCheckService.getMyUniqueId() != lookBook.user.id

            if(position <3){
                binding.cardName.text = "Best ${position+1}"
            }
            binding.likes = lookBook.clothesInfo.count?.favorites
            binding.clothPost = lookBook

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