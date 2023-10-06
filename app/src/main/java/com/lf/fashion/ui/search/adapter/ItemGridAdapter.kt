package com.lf.fashion.ui.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.model.Cloth
import com.lf.fashion.databinding.ItemSearchResultItemListBinding
import com.lf.fashion.ui.globalFrag.adapter.GridPhotoClickListener
import com.lf.fashion.ui.globalFrag.adapter.SpanCountEditBtnListener
import com.lf.fashion.ui.common.itemViewRatioSetting

class ItemGridAdapter(
    private var spanCount: Int? = null,
    private val gridPhotoClickListener: GridPhotoClickListener,
) : ListAdapter<Cloth, ItemGridAdapter.ItemGridViewHolder>(object : DiffUtil.ItemCallback<Cloth>() {
    override fun areItemsTheSame(oldItem: Cloth, newItem: Cloth): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cloth, newItem: Cloth): Boolean {
        return oldItem == newItem
    }

}),
    SpanCountEditBtnListener {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemGridViewHolder {
        val binding = ItemSearchResultItemListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        context = parent.context
        return ItemGridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemGridViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class ItemGridViewHolder(private val binding: ItemSearchResultItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cloth) {

            binding.includedClothSpace.photoUrl = item.imageUrl
            // 현재 spanCount에 따라 너비와 높이를 조정
            itemViewRatioSetting(context, itemView, spanCount, reduceViewWidth = true)

            binding.includedClothSpace.gridImage.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.executePendingBindings()

            binding.root.setOnClickListener {
                gridPhotoClickListener.gridPhotoClicked(currentList.indexOf(item))
            }
        }
    }

    override fun editSpanCountBtnClicked(newSpan: Int) {
        spanCount = newSpan
    }

}


