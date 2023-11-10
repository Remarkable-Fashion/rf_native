package com.lf.fashion.ui.globalFrag.editPost

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.TAG
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.databinding.ItemImageBinding
import com.lf.fashion.ui.home.adapter.PhotoDiff

class EditPostAdapter(
) :
    ListAdapter<ImageUrl, EditPostAdapter.EditPostViewHolder>(PhotoDiff()) {

    private lateinit var binding: ItemImageBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditPostViewHolder {
        binding =
            ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EditPostViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.checkbox.isVisible = false
        }

        fun bind(image: ImageUrl) {
           binding.imageUrl = image.url
        }
    }
}


