package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.Posts
import com.lf.fashion.databinding.HomeBottomDialogItemBinding
import com.lf.fashion.ui.PrefCheckService
import com.lf.fashion.ui.home.PostBottomViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

/**
 * 공유 버튼 클릭시 노출되는 바텀 다이얼로그 시트입니다
 */
@AndroidEntryPoint
class PostBottomSheetFragment(private val post: Posts) : BottomSheetDialogFragment(),
    View.OnClickListener {
    private lateinit var binding: HomeBottomDialogItemBinding
    private lateinit var userPref: PreferenceManager
    private lateinit var prefCheckService: PrefCheckService
    private val viewModel: PostBottomViewModel by viewModels()
    private var blocked: Boolean = false // 최초값 차단 false 로 ..
    private var scrapState by Delegates.notNull<Boolean>()
    private var followState by Delegates.notNull<Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBottomDialogItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPref = PreferenceManager(requireContext().applicationContext)
        prefCheckService = PrefCheckService(userPref)

        //로그인 유저에게 차단/팔로우 취소 버튼 노출
        loginUserUi()

        binding.bottomLayout.children.forEach { it.setOnClickListener(this) }
        binding.bottomLinear.children.forEach { it.setOnClickListener(this) }

        scrapState = post.isScrap == true
        followState = post.isFollow == true

        btnTextUpdate("scrap", scrapState)
        btnTextUpdate("follow", followState)
        btnTextUpdate("block", blocked)
        observeAllMsgResponse()

    }

    private fun btnTextUpdate(name: String, state: Boolean) {
        when (name) {
            "scrap" -> {
                binding.bottomSheetScrapBtn.text = if (state) "스크랩취소" else "스크랩하기"
            }
            "follow" -> {
                binding.bottomSheetFollowBtn.text = if (state) "팔로우 취소" else "팔로잉하기"
            }
            "block" -> {
                binding.blockBtn.text = if (state) "차단 취소" else "차단하기"

            }
        }
    }

    private fun loginUserUi() {
        if (prefCheckService.loginCheck()) {
            binding.bottomSheetFollowBtn.isVisible = true
            binding.blockBtn.isVisible = true
        } else {
            binding.bottomSheetFollowBtn.isVisible = false
            binding.blockBtn.isVisible = false
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.bottomSheetLinkCopyBtn -> {

            }
            binding.bottomSheetShareBtn -> {

            }
            binding.bottomSheetScrapBtn -> {
                viewModel.changeScrapState(!scrapState, post.id)
            }
            binding.noInterestBtn -> {

            }
            binding.bottomSheetFollowBtn -> {
                viewModel.changeFollowingState(!followState, post.user!!.id)
            }
            binding.blockBtn -> {
                viewModel.changeBlockUserState(!blocked, post.user!!.id)
            }
            binding.declareBtn -> {

            }
        }
    }

    private fun observeAllMsgResponse() {
        viewModel.followResponse.observe(viewLifecycleOwner) {
            if ((it is Resource.Success) && it.value.success) {
                followState = !followState
                btnTextUpdate("follow", followState)
            }
        }

        viewModel.scrapResponse.observe(viewLifecycleOwner) {
            if ((it is Resource.Success) && it.value.success) {
                scrapState = !scrapState
                btnTextUpdate("scrap", scrapState)
            }
        }
        viewModel.blockResponse.observe(viewLifecycleOwner) {
            if ((it is Resource.Success) && it.value.success) {
                blocked = !blocked
                btnTextUpdate("block", blocked)
            }
        }
    }
}