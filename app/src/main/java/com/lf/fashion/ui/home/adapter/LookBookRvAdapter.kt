package com.lf.fashion.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.model.ClothPost
import com.lf.fashion.databinding.ItemRecommendStyleCardBinding
import com.lf.fashion.ui.home.ClothLikeClickListener

class LookBookRvAdapter(
    private val kebabOnClick: (Int) -> Unit,
    private val clothLikeClickListener: ClothLikeClickListener
) : ListAdapter<ClothPost, LookBookRvAdapter.LookBookViewHolder>(LookBookDiff()) {
    private lateinit var userPref: PreferenceManager
    private var orderByMode: String = "Best" // 초기 상태는 Default로 설정
    private lateinit var binding :ItemRecommendStyleCardBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookBookViewHolder {
        binding = ItemRecommendStyleCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        userPref = PreferenceManager(parent.context.applicationContext)

        return LookBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookBookViewHolder, position: Int) {
        holder.bind(getItem(position), position)
        }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUI(newOrderByMode: String) {
        orderByMode = newOrderByMode
        notifyDataSetChanged()
    }

    inner class LookBookViewHolder(val binding: ItemRecommendStyleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lookBook: ClothPost, position: Int) {
            if(orderByMode=="Best"){
                if (position < 3) {
                    binding.cardName.text = "Best ${position + 1}"
                }
            }else{
                binding.cardName.text = ""
            }
            binding.profileSpace.followBtn.isVisible = userPref.getMyUniqueId() != lookBook.user.id

            binding.likeBtn.isSelected = lookBook.isFavorite ?: false
            binding.likesValue.text = lookBook.clothesInfo.count?.favorites.toString()

            binding.clothPost = lookBook

            binding.profileSpace.profile = lookBook.user

            binding.clothesSpace.cloth = lookBook.clothesInfo
            binding.likeBtn.setOnClickListener {
                //    binding.likeBtn.isSelected = !binding.likeBtn.isSelected
                clothLikeClickListener.clothLikeBtnClicked(it.isSelected, lookBook)
            }
            binding.profileSpace.kebabBtn.setOnClickListener {
                kebabOnClick(lookBook.user.id)
            }
        }
    }
}

class LookBookDiff : DiffUtil.ItemCallback<ClothPost>() {
    override fun areItemsTheSame(oldItem: ClothPost, newItem: ClothPost): Boolean {
        return oldItem.clothesInfo.id == newItem.clothesInfo.id
    }

    override fun areContentsTheSame(oldItem: ClothPost, newItem: ClothPost): Boolean {
        return oldItem.copy(
            isFavorite = newItem.isFavorite,
            clothesInfo = newItem.clothesInfo
        ) == newItem
    }

    override fun getChangePayload(oldItem: ClothPost, newItem: ClothPost): Any? {
        val payload = mutableSetOf<String>()

        if (oldItem.isFavorite != newItem.isFavorite) {
            payload.add("IS_FAVORITE")
        }

        if (oldItem.clothesInfo.count?.favorites != newItem.clothesInfo.count?.favorites) {
            payload.add("FAVORITES_COUNT")
        }

        return super.getChangePayload(oldItem, newItem)

    }
}