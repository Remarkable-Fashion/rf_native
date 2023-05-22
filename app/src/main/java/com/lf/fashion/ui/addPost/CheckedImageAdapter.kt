package com.lf.fashion.ui.addPost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lf.fashion.data.response.ImageItem
import com.lf.fashion.databinding.ItemSelectedImageBinding

//커스텀 갤러리 내부의 사진을 선택시 상단에 노출되는 선택 사진 미리보기 recyclerView Adapter 입니다.
class CheckedImageAdapter(private val checkedImageCancelBtnListener: CheckedImageRVListener) :
    ListAdapter<ImageItem, CheckedImageAdapter.CheckedImageViewHolder>(ImageDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckedImageViewHolder {
        val binding =
            ItemSelectedImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = CheckedImageViewHolder(binding)
        rvClickEvent(binding,holder)
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
    private fun rvClickEvent(binding : ItemSelectedImageBinding, holder : CheckedImageViewHolder){
        //선택된 사진의 x 버튼을 클릭
        binding.selectedCancelBtn.setOnClickListener {
            val position = holder.absoluteAdapterPosition
            val newList = currentList.toMutableList()
            checkedImageCancelBtnListener.checkedCancel(newList[position])
            newList.removeAt(position)
            submitList(newList)

        }
    }
}