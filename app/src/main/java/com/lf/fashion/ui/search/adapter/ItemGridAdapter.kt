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
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.SpanCountEditBtnListener
import com.lf.fashion.ui.convertDPtoPX

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
        private val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams

        fun bind(item: Cloth) {

            binding.includedClothSpace.photoUrl = item.imageUrl
            layoutParams.height = when (spanCount ?: 2) {
                2 -> convertDPtoPX(context, 300)
                3 -> convertDPtoPX(context, 250)
                else -> layoutParams.height

            }
            binding.includedClothSpace.gridImage.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.executePendingBindings()
        }
    }

    override fun editSpanCountBtnClicked(newSpan: Int) {
        spanCount = newSpan
    }

}


