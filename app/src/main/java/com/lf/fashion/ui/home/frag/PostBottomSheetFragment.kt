package com.lf.fashion.ui.home.frag

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lf.fashion.R
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.Posts
import com.lf.fashion.databinding.HomeBottomDialogItemBinding
import com.lf.fashion.ui.MyBottomDialogListener
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

/**
 * 공유 버튼 클릭시 노출되는 바텀 다이얼로그 시트입니다
 */
@AndroidEntryPoint
class PostBottomSheetFragment(private val post: Posts? = null, private val userId: Int? = null) :
    BottomSheetDialogFragment(R.layout.home_bottom_dialog_item),
    View.OnClickListener {
    private lateinit var binding: HomeBottomDialogItemBinding
    private lateinit var userPref: PreferenceManager
    private val viewModel: PostBottomViewModel by viewModels()
    private var blocked: Boolean = false // 최초값 차단 false 로 ..
    private var scrapState by Delegates.notNull<Boolean>()
    private var followState by Delegates.notNull<Boolean>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBottomDialogItemBinding.bind(view)

        userPref = PreferenceManager(requireContext().applicationContext)

        binding.bottomLayout.children.forEach { it.setOnClickListener(this) }
        binding.bottomLinear.children.forEach { it.setOnClickListener(this) }
        if (post != null) {
            //로그인 유저에게 차단/팔로우 취소 버튼 노출
            loginUserUi()
            //나의 게시물일 경우 게시물 관련 버튼 노출
            myPostBottomUi()

            viewModel.getPostByPostId(post.id)
            viewModel.postInfo.observe(viewLifecycleOwner) { resources ->
                if (resources is Resource.Success) {
                    val response = resources.value

                    scrapState = response.isScrap ?: false
                    followState = response.isFollow ?: false

                    btnTextUpdate("scrap", scrapState)
                    btnTextUpdate("follow", followState)
                    btnTextUpdate("block", blocked)
                }
            }
            observeAllMsgResponse()
        } else {
            binding.declareBtn.isVisible = userPref.getMyUniqueId() != userId
            binding.bottomSheetFollowBtn.isVisible = false
            binding.blockBtn.isVisible = false
            binding.privateSettingBtn.isVisible = false
            binding.postEditBtn.isVisible = false
            binding.deleteBtn.isVisible = false
            binding.bottomSheetScrapBtn.isVisible = false

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        post?.let {
            it.isScrap = scrapState
            //  post.isFollow = followState follow는 바꿀필요 없을 것 같 ui 에서 바로 노출 x
            (parentFragment as? MyBottomDialogListener)?.onBottomSheetDismissed(it)
        }
        super.onDismiss(dialog)
    }

    private fun loginUserUi() {
        val loggedIn = userPref.loginCheck()
        binding.bottomSheetFollowBtn.isVisible = loggedIn
        binding.blockBtn.isVisible = loggedIn

    }

    private fun myPostBottomUi() {
        //나의 게시물일 경우 myPost 는 true
        val myPost = userPref.getMyUniqueId() == post!!.user?.id

        binding.declareBtn.isVisible = !myPost
        binding.bottomSheetFollowBtn.isVisible = !myPost
        binding.blockBtn.isVisible = !myPost
        binding.privateSettingBtn.isVisible = myPost
        binding.postEditBtn.isVisible = myPost
        binding.deleteBtn.isVisible = myPost

    }

    //TODO 버튼 반응 구현
    override fun onClick(view: View?) {
        when (view) {
            binding.bottomSheetLinkCopyBtn -> {

            }

            binding.bottomSheetShareBtn -> {

            }

            binding.bottomSheetScrapBtn -> {
                viewModel.changeScrapState(!scrapState, post!!.id)
            }
            /* binding.noInterestBtn -> {
                 // 이 후 동작 알 수 없음 , 엔드포인트 없음
             }*/
            binding.bottomSheetFollowBtn -> {
                viewModel.changeFollowingState(!followState, post!!.user!!.id)
            }

            binding.blockBtn -> {
                viewModel.changeBlockUserState(!blocked, post!!.user!!.id)
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
}