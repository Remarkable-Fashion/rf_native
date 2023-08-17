package com.lf.fashion.ui.mypage.followDetail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.OtherUser
import com.lf.fashion.databinding.MypageFollowViewpagerBinding
import com.lf.fashion.ui.mypage.MyPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowDetailItemFragment(private val tabName: String) :
    Fragment(R.layout.mypage_follow_viewpager) {
    private lateinit var binding: MypageFollowViewpagerBinding
    private val viewModel: MyPageViewModel by hiltNavGraphViewModels(R.id.navigation_mypage)
    private val userListAdapter = UserListAdapter()
    private lateinit var currentUserList: List<OtherUser>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MypageFollowViewpagerBinding.bind(view)

        binding.profileRV.adapter = userListAdapter

        when (tabName) {
            "follower" -> {
                viewModel.getMyFollowers()
                viewModel.myFollowers.observe(viewLifecycleOwner) { resources ->
                    if (resources is Resource.Success) {
                        val followers = resources.value.followers
                        updateEmptyText(followers.isNullOrEmpty(), "팔로워가 없습니다.")
                        followers?.let { currentUserList = it }
                        userListAdapter.submitList(followers)
                    }
                }

            }

            "following" -> {
                viewModel.getMyFollowings()
                viewModel.myFollowings.observe(viewLifecycleOwner) { resources ->
                    if (resources is Resource.Success) {
                        val followings = resources.value.followings
                        updateEmptyText(followings.isNullOrEmpty(), "팔로잉이 없습니다.")
                        followings?.let  { currentUserList = it }
                        userListAdapter.submitList(followings)
                    }
                }
            }

            "block" -> {
                viewModel.getMyBlockUsers()
                viewModel.myBlockUsers.observe(viewLifecycleOwner) { resources ->
                    if (resources is Resource.Success) {
                        val blockedUsers = resources.value.blockedUsers
                        updateEmptyText(blockedUsers.isNullOrEmpty(), "차단하신 유저가 없습니다.")
                        blockedUsers?.let  { currentUserList = it }
                        userListAdapter.submitList(blockedUsers)
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val searchEditText = binding.searchEt
            val editTextToFlow = editTextToFlow(searchEditText)
            editTextToFlow.debounce(500)
               // .filter { it?.length!! > 0 }
                .onEach {
                    if(it?.length!! >0) {
                        searchUser(it.toString())
                    }else{
                        userListAdapter.submitList(currentUserList) // 검색창 empty 시 이전 list 재전송
                    }
                }.launchIn(this)

        }
    }

    private fun updateEmptyText(isEmpty: Boolean, text: String) {
        if (isEmpty) {
            binding.arrayEmptyText.text = text
            binding.arrayEmptyText.isVisible = true
        } else {
            binding.arrayEmptyText.isVisible = false
        }
    }

    //Debounce 해야함
    //observe한 list들을 상단 객체에 넣어두고 contain 으로 검색하기?
    private suspend fun editTextToFlow(editText: EditText): Flow<CharSequence?> {
        return callbackFlow<CharSequence?> {
            val listener = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(
                    text: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    trySend(text)
                }

                override fun afterTextChanged(s: Editable?) = Unit

            }
            editText.addTextChangedListener(listener)
            awaitClose {
                editText.removeTextChangedListener(listener)
            }

        }.onStart {
            Log.d(TAG, "textChangesToFlow() / onStart 발동")
            emit(editText.text)
        }
    }

    private fun searchUser(term: String) {
        val resultUser = mutableListOf<OtherUser>()
        currentUserList.forEach {
            if (it.user.name.contains(term)) {
                resultUser.add(it)
            }
        }
        userListAdapter.submitList(resultUser)
    }

}