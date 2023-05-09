package com.lf.fashion.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.installations.Utils
import com.lf.fashion.databinding.HomeNestedHorizontalItemBinding

class PhotoHorizontalAdapter :
    ListAdapter<String, PhotoHorizontalAdapter.PhotoHorizontalViewHolder>(PhotoDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHorizontalViewHolder {
        val binding = HomeNestedHorizontalItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PhotoHorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoHorizontalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoHorizontalViewHolder(private val binding: HomeNestedHorizontalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: String){
            binding.photo = photo
        }
    }
}

class PhotoDiff : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}