package com.lf.fashion.ui.globalFrag.editPost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.model.ImageUrl
import com.lf.fashion.databinding.ItemSelectedImageBinding
import com.lf.fashion.ui.home.adapter.PhotoDiff

class EditPhotoRvAdapter : ListAdapter<ImageUrl, EditPhotoRvAdapter.EditPostRvViewHolder>(PhotoDiff()){
    private lateinit var binding : ItemSelectedImageBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditPostRvViewHolder {
        binding = ItemSelectedImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return EditPostRvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditPostRvViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EditPostRvViewHolder(binding : ItemSelectedImageBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(image : ImageUrl){
            binding.imageUrl = image.url

            binding.selectedCancelBtn.setOnClickListener {

            }
        }
    }

}
