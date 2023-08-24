package com.lf.fashion.ui.mypage.followDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.OtherUser
import com.lf.fashion.databinding.ItemFollowerBinding
import com.lf.fashion.ui.mypage.MyPageViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserListAdapter(private val tabName: String, private val viewModel: MyPageViewModel) :
    ListAdapter<OtherUser, UserListAdapter.UserListViewHolder>(object :
        DiffUtil.ItemCallback<OtherUser>() {
        override fun areItemsTheSame(oldItem: OtherUser, newItem: OtherUser): Boolean {
            return oldItem.user.id == newItem.user.id
        }

        override fun areContentsTheSame(oldItem: OtherUser, newItem: OtherUser): Boolean {
            return oldItem == newItem
        }

    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val binding =
            ItemFollowerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class UserListViewHolder(private val binding: ItemFollowerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wrap: OtherUser) {
            binding.profile = wrap.user
            binding.cancelBtn.apply {
                when (tabName) {
                    "follower" -> {
                        this.text = "삭제"
                        this.setOnClickListener {
                            // TODO 아직 엔드포인트 없음
                        }
                    }

                    "following" -> {
                        this.text = "취소"
                        this.setOnClickListener {
                            runBlocking {
                                launch {
                                    val response =
                                        viewModel.changeFollowingState(create = false, wrap.user.id)
                                    if(response is Resource.Success && response.value.success){
                                        Toast.makeText(context,"팔로우가 취소되었습니다.",Toast.LENGTH_SHORT).show()
                                        val newList = currentList.toMutableList()
                                        newList.remove(wrap)
                                        submitList(newList)
                                    }
                                }
                            }
                        }
                    }

                    "block" -> {
                        this.text = "차단해제"
                        this.setOnClickListener {
                            runBlocking {
                                launch {
                                    val response =
                                        viewModel.changeBlockUserState(create = false, wrap.user.id)
                                    if(response is Resource.Success && response.value.success){
                                        Toast.makeText(context,"차단이 해제되었습니다.",Toast.LENGTH_SHORT).show()
                                        val newList = currentList.toMutableList()
                                        newList.remove(wrap)
                                        submitList(newList)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}