package com.lf.fashion.ui.addPost

import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ImageItem
import com.lf.fashion.databinding.ItemCameraBinding
import com.lf.fashion.databinding.ItemImageBinding

/**
 * @param parentViewModel The ImagePicker's ViewModel which holds each ImageItem
 * whose isChecked should be updated when checkbox checked.
 */
class ImageAdapter(
    private val parentViewModel: ImagePickerViewModel,private val imageCheckedListener: ImageCheckedListener
) : ListAdapter<ImageItem, RecyclerView.ViewHolder>(ImageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_FIRST_ITEM = 1
        private const val VIEW_TYPE_DEFAULT_ITEM = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //var holder = null
        return when (viewType) {
            VIEW_TYPE_FIRST_ITEM -> {
                val binding =
                    ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_FIRST_ITEM) {
            (holder as FirstImageViewHolder).bind()
        } else {
            val imageItem = getItem(position)
            (holder as ImageViewHolder).bind(imageItem)
        }
    }


    override fun submitList(list: List<ImageItem>?) {
        // 리스트를 제출할 때 첫 번째 아이템을 추가하여 밀어내는 효과를 줍니다.
        val newList = mutableListOf<ImageItem>()
        newList.add(ImageItem(null, false)) // 첫 번째 아이템 추가
        if (list != null) {
            newList.addAll(list) // 나머지 아이템 추가
        }
        super.submitList(newList)
    }

    //이미지 로드&바인딩 로직
    class ImageViewHolder(
        private val binding: ItemImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageItem: ImageItem) {
            Glide.with(binding.root)
                .load(imageItem.uri)
                .into(binding.image)

            binding.checkbox.isChecked = imageItem.isChecked
           // Log.d(TAG, "ImageViewHolder - bind: $imageItem");
        }
    }

    private fun subscribeUi(binding: ItemImageBinding, holder: ImageViewHolder) {
        binding.image.setOnClickListener {
            parentViewModel.imageItemList.value?.let {
                val position = holder.absoluteAdapterPosition
                binding.checkbox.isChecked = !binding.checkbox.isChecked // 체크박스 체크 여부 반전
                it[position - 1].isChecked = binding.checkbox.isChecked // 체크 여부를 객체에도 담아줌
                imageCheckedListener.imageChecked(it[position-1])
            }
        }
    }

    class FirstImageViewHolder(
        private val binding: ItemCameraBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            VIEW_TYPE_FIRST_ITEM
        } else {
            VIEW_TYPE_DEFAULT_ITEM
        }
    }
}

 class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.uri == newItem.uri
    }
}