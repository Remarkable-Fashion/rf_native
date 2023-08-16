package com.lf.fashion.ui.mypage.followDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.response.OtherUser
import com.lf.fashion.databinding.ItemFollowerBinding

class UserListAdapter : ListAdapter<OtherUser,UserListAdapter.UserListViewHolder>(object : DiffUtil.ItemCallback<OtherUser>(){
    override fun areItemsTheSame(oldItem: OtherUser, newItem: OtherUser): Boolean {
        return oldItem.user.id == newItem.user.id
    }

    override fun areContentsTheSame(oldItem: OtherUser, newItem: OtherUser): Boolean {
        return oldItem == newItem
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val binding = ItemFollowerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserListViewHolder(private val binding : ItemFollowerBinding ) : RecyclerView.ViewHolder(binding.root){
        fun bind(wrap : OtherUser){
            binding.profile = wrap.user
        }
    }
}