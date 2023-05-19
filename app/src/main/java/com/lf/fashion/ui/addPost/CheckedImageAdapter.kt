package com.lf.fashion.ui.addPost

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ImageItem
import com.lf.fashion.databinding.ItemSelectedImageBinding

class CheckedImageAdapter(private val checkedImageCancelBtnListener: CheckedImageRVListener) :
    ListAdapter<ImageItem, CheckedImageAdapter.CheckedImageViewHolder>(ImageDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckedImageViewHolder {
        val binding =
            ItemSelectedImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = CheckedImageViewHolder(binding)
        selectedCancel(binding,holder)
        return holder
    }

    override fun onBindViewHolder(holder: CheckedImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CheckedImageViewHolder(private val binding: ItemSelectedImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imageItem: ImageItem) {
            Glide.with(binding.root)
                .load(imageItem.uri)
                .into(binding.image)
        }
    }
    private fun selectedCancel(binding : ItemSelectedImageBinding,holder : CheckedImageViewHolder){
        binding.selectedCancelBtn.setOnClickListener {
            val position = holder.absoluteAdapterPosition
            val newList = currentList.toMutableList()
            checkedImageCancelBtnListener.checkedCancel(newList[position])
            newList.removeAt(position)
            submitList(newList)

        }
    }
}