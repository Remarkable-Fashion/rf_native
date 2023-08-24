package com.lf.fashion.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.databinding.HomeANestedHorizontalItemBinding
import com.lf.fashion.ui.home.PhotoClickListener

/**
 * HomeFragment _ DefaultPostAdapter 와 PhotoDetailFragment 에서 사용
 */
class PhotoHorizontalAdapter(private val photoClickListener: PhotoClickListener?) :
    ListAdapter<ImageUrl, PhotoHorizontalAdapter.PhotoHorizontalViewHolder>(PhotoDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHorizontalViewHolder {
        val binding = HomeANestedHorizontalItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoHorizontalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoHorizontalViewHolder(private val binding: HomeANestedHorizontalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: ImageUrl) {
            binding.photo = photo.url
             binding.photoImageView.setOnClickListener {
                 photoClickListener?.photoClicked(true, currentList)
             }

            binding.executePendingBindings()

        }
    }
}

class PhotoDiff : DiffUtil.ItemCallback<ImageUrl>() {
    override fun areItemsTheSame(oldItem: ImageUrl, newItem: ImageUrl): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: ImageUrl, newItem: ImageUrl): Boolean {
        return oldItem == newItem
    }
}