package com.lf.fashion.ui.home.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.TAG
import com.lf.fashion.data.response.Photo
import com.lf.fashion.databinding.HomeNestedHorizontalItemBinding
import com.lf.fashion.ui.home.PhotoClickListener

/**
 * HomeFragment _ DefaultPostAdapter 와 PhotoDetailFragment 에서 사용
 */
class PhotoHorizontalAdapter(private val photoClickListener: PhotoClickListener?) :
    ListAdapter<Photo, PhotoHorizontalAdapter.PhotoHorizontalViewHolder>(PhotoDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHorizontalViewHolder {
        val binding = HomeNestedHorizontalItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoHorizontalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoHorizontalViewHolder(private val binding: HomeNestedHorizontalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo) {
            binding.photo = photo.imageUrl
             binding.photoImageView.setOnClickListener {
                 photoClickListener?.photoClicked(true, currentList)
             }
        }
    }
}

class PhotoDiff : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }
}