package com.lf.fashion.ui.addPost

import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lf.fashion.R
import com.lf.fashion.data.response.ImageItem
import com.lf.fashion.databinding.ItemCameraBinding
import com.lf.fashion.databinding.ItemImageBinding

/**
 * @param parentViewModel The ImagePicker's ViewModel which holds each ImageItem
 * whose isChecked should be updated when checkbox checked.
 */
class ImageAdapter(
    private val parentViewModel: ImagePickerViewModel
) : ListAdapter<ImageItem, RecyclerView.ViewHolder>(ImageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_FIRST_ITEM = 1
        private const val VIEW_TYPE_DEFAULT_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //var holder = null
        return when (viewType) {
            VIEW_TYPE_FIRST_ITEM -> {
                val binding = ItemCameraBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                FirstImageViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemImageBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_image,
                    parent,
                    false
                )

                val holder = ImageViewHolder(binding)
                subscribeUi(binding, holder)
                holder
            }
        }
        //return holder
    }

    private fun subscribeUi(binding: ItemImageBinding, holder: ImageViewHolder) {
        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            parentViewModel.imageItemList.value?.let {
                val position = holder.absoluteAdapterPosition
                it[position].isChecked = isChecked
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_FIRST_ITEM){
            (holder as FirstImageViewHolder).bind()
        }else {
            val imageItem = getItem(position)
            (holder as ImageViewHolder).bind(imageItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_FIRST_ITEM
        } else {
            VIEW_TYPE_DEFAULT_ITEM
        }
    }

    override fun submitList(list: List<ImageItem>?) {
        // 리스트를 제출할 때 첫 번째 아이템을 추가하여 밀어내는 효과를 줍니다.
        val newList = mutableListOf<ImageItem>()
        newList.add(ImageItem(null,false)) // 첫 번째 아이템 추가
        if (list != null) {
            newList.addAll(list) // 나머지 아이템 추가
        }
        super.submitList(newList)
    }
    class ImageViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageItem: ImageItem) {
            Glide.with(binding.root)
                .load(imageItem.uri)
                .into(binding.image)
        }
    }

    class FirstImageViewHolder(
        private val binding : ItemCameraBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(){

        }
    }
}

private class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem == newItem
    }
}