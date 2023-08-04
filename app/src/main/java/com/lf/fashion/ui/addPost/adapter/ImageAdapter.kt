package com.lf.fashion.ui.addPost.adapter

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
import com.lf.fashion.ui.addPost.GalleryRvListener
import com.lf.fashion.ui.addPost.ImagePickerViewModel

/**
 * @param parentViewModel The ImagePicker's ViewModel which holds each ImageItem
 * whose isChecked should be updated when checkbox checked.
 */
class ImageAdapter(
    private val parentViewModel: ImagePickerViewModel,
    private val galleryRvListener: GalleryRvListener,
    private val imageLimit: Int
) : ListAdapter<ImageItem, RecyclerView.ViewHolder>(ImageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_FIRST_ITEM = 1
        private const val VIEW_TYPE_DEFAULT_ITEM = 2
    }

    private var checkedImageCounter = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FIRST_ITEM -> { // 첫번째 아이템은 카메라 실행 ui 로 !
                val binding =
                    ItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = FirstImageViewHolder(binding)
                cameraBtnClicked(binding)
                holder
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
    }

    private fun subscribeUi(binding: ItemImageBinding, holder: ImageViewHolder) {
        binding.image.setOnClickListener {
            parentViewModel.imageItemList.value?.let { it ->
                if (checkedImageCounter in 0 until imageLimit) {
                    checkBoxConverse(holder, binding, it)
                    checkedImageCounter += 1 // 사진 선택 갯수 제한에 쓰임
                } else {
                    galleryRvListener.checkedCountOver(imageLimit)
                }
            }
        }
    }

    private fun checkBoxConverse(
        holder: ImageViewHolder,
        binding: ItemImageBinding,
        itemList: MutableList<ImageItem>
    ) {
        val position = holder.absoluteAdapterPosition
        binding.checkbox.isSelected = !binding.checkbox.isSelected // 체크박스 체크 여부 반전
        itemList[position - 1].isChecked = binding.checkbox.isSelected // 체크 여부를 객체에도 담아줌
        galleryRvListener.imageChecked(itemList[position - 1])

    }

    private fun cameraBtnClicked(binding: ItemCameraBinding) {
        binding.camera.setOnClickListener {
            galleryRvListener.cameraBtnClicked()
        }
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
        newList.add(ImageItem(null, false, "")) // 첫 번째 아이템 추가
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

            binding.checkbox.isSelected = imageItem.isChecked
            binding.checkbox.text = imageItem.checkCount.ifEmpty { "" }
        }
    }

    class FirstImageViewHolder(
        private val binding: ItemCameraBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {}
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