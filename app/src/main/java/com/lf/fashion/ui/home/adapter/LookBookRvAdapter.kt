package com.lf.fashion.ui.home.adapter

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

class LookBookRvAdapter(private val kebabOnClick :(Int)-> Unit , private val clothLikeClickListener: ClothLikeClickListener) : ListAdapter<ClothPost, LookBookRvAdapter.LookBookViewHolder>(LookBookDiff()) {
    private lateinit var userPref: PreferenceManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookBookViewHolder {
       val binding = ItemRecommendStyleCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        userPref = PreferenceManager(parent.context.applicationContext)

        return LookBookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LookBookViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

    inner class LookBookViewHolder(private val binding: ItemRecommendStyleCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lookBook: ClothPost,position : Int) {

            binding.profileSpace.followBtn.isVisible = userPref.getMyUniqueId() != lookBook.user.id

            if(position <3){
                binding.cardName.text = "Best ${position+1}"
            }
            binding.likeBtn.isSelected = lookBook.isFavorite?:false
            binding.likesValue.text = lookBook.clothesInfo.count?.favorites.toString()

            binding.clothPost = lookBook

            binding.profileSpace.profile = lookBook.user

            binding.clothesSpace.cloth = lookBook.clothesInfo
            binding.likeBtn.setOnClickListener {
            //    binding.likeBtn.isSelected = !binding.likeBtn.isSelected
                clothLikeClickListener.clothLikeBtnClicked(it.isSelected,lookBook)
            }
            binding.profileSpace.kebabBtn.setOnClickListener {
              kebabOnClick(lookBook.user.id)
            }
        }
    }
}
//TODO diff TEST 필요, 좋아요 selected 잘 변경 되는지 + like delete 도 api 추가 및 테스트 필요 !
class LookBookDiff : DiffUtil.ItemCallback<ClothPost>() {
    override fun areItemsTheSame(oldItem: ClothPost, newItem: ClothPost): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ClothPost, newItem: ClothPost): Boolean {
        return oldItem.copy(isFavorite = newItem.isFavorite, clothesInfo = newItem.clothesInfo) == newItem
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