package com.lf.fashion.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.model.SearchTerm
import com.lf.fashion.databinding.ItemSearchTermRankBinding

class TermRankAdapter(private val searchRankRowClickListener: SearchRankRowClickListener) :
    ListAdapter<SearchTerm, TermRankAdapter.TermRankViewHolder>(RankTermDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermRankViewHolder {
        val binding =
            ItemSearchTermRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TermRankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TermRankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TermRankViewHolder(private val binding: ItemSearchTermRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchTerm) {
            binding.popularTerm = item.term
            binding.rank = (currentList.indexOf(item) + 1).toString()
            when (item.changeIndicator) {
                "─" -> {
                    updateChangeIndicator(true, up = false, false)
                }
                else -> {
                    if (item.changeIndicator.contains("+")) { //상향
                        updateChangeIndicator(false, up = true, false)
                    } else { //하향
                        updateChangeIndicator(false, up = false, true)
                    }
                }
            }

            binding.row.setOnClickListener {
                searchRankRowClickListener.searchRankOnclick(item.term)
            }
        }

        private fun updateChangeIndicator(notChanged: Boolean, up: Boolean, down: Boolean) {
            binding.notChanged.isVisible = notChanged
            binding.up.isVisible = up
            binding.down.isVisible = down

        }
    }

}

class RankTermDiff : DiffUtil.ItemCallback<SearchTerm>() {
    override fun areItemsTheSame(oldItem: SearchTerm, newItem: SearchTerm): Boolean {
        return oldItem.term == newItem.term

    }

    override fun areContentsTheSame(oldItem: SearchTerm, newItem: SearchTerm): Boolean {
        return oldItem == newItem
    }

}

interface SearchRankRowClickListener{
    fun searchRankOnclick(term : String)
}