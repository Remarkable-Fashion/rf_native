package com.lf.fashion.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.databinding.ItemSearchTermRankBinding

class TermRankAdapter : ListAdapter<String, TermRankAdapter.TermRankViewHolder>(RankTermDiff()) {

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
        fun bind(term : String){
            binding.popularTerm = term
            binding.rank = (currentList.indexOf(term)+1).toString()
        }
    }

}
class RankTermDiff:DiffUtil.ItemCallback<String>(){
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem

    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}