package com.lf.fashion.ui.globalFrag.bottomsheet

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.model.DeclareInfo
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.Posts
import com.lf.fashion.databinding.HomeBottomDialogItemBinding
import com.lf.fashion.ui.common.MyBottomDialogListener
import com.lf.fashion.ui.common.showRequireLoginDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 공유 버튼 클릭시 노출되는 바텀 다이얼로그 시트입니다
 */
@AndroidEntryPoint
class PostBottomSheetFragment(
    private val post: Posts? = null,
    private val userId: Int? = null,
    private val myBottomDialogListener: MyBottomDialogListener? = null,
    private val userShareOnclick: (() -> Unit)? = null
) :
    BottomSheetDialogFragment(R.layout.home_bottom_dialog_item),
    View.OnClickListener {
    private lateinit var binding: HomeBottomDialogItemBinding
    private lateinit var userPref: UserDataStorePref
    private val viewModel: PostBottomViewModel by viewModels()
    private var blocked: Boolean = false // 최초값 차단 false 로 ..
    private var scrapState = false
    private var followState = false
    private var isPublicState = post?.isPublic ?: true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBottomDialogItemBinding.bind(view)

        userPref = UserDataStorePref(requireContext().applicationContext)

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
                    Log.e(TAG, "onViewCreated: $response")
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
            myBottomDialogListener?.onBottomSheetDismissed(it)
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
        val postUserId = post?.user?.id ?: userId
        val myPost = userPref.getMyUniqueId() == postUserId

        binding.declareBtn.isVisible = !myPost
        binding.bottomSheetFollowBtn.isVisible = !myPost
        binding.blockBtn.isVisible = !myPost
        binding.privateSettingBtn.isVisible = myPost
        binding.postEditBtn.isVisible = myPost
        binding.deleteBtn.isVisible = myPost
        btnTextUpdate("isPublic", isPublicState)
    }

    //TODO 버튼 반응 구현
    override fun onClick(view: View?) {
        when (view) {
            binding.bottomSheetLinkCopyBtn -> {

            }

            binding.bottomSheetShareBtn -> {
                myBottomDialogListener?.shareBtn(post!!)
                if (myBottomDialogListener == null) { //유저 페이지 공유
                    userShareOnclick?.let {
                        it()
                    }
                }
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
                showDeclareDialog()

            }

            binding.deleteBtn -> {
                post?.let {
                    myBottomDialogListener?.deleteMyPost(it)
                    dismiss()
                }
            }

            binding.privateSettingBtn -> {
                post?.let {
                    Log.e(TAG, "onClick: 게시 상태 수정 클릭 $it")
                    myBottomDialogListener?.changePostPublicStatus(it)
                    dismiss()
                }
            }

            binding.postEditBtn -> {
                post?.let {
                    Log.e(TAG, "onClick: 게시물 수정 클릭 $it")
                    myBottomDialogListener?.editPost(it)
                    dismiss()
                }
            }
        }
    }

    private fun observeAllMsgResponse() {
        // 로그인한 사용자에게만 노출
        val loginCheck = userPref.loginCheck()
        binding.bottomSheetScrapBtn.isVisible = loginCheck
        binding.bottomSheetFollowBtn.isVisible = loginCheck
        binding.blockBtn.isVisible = loginCheck
        binding.declareBtn.isVisible = loginCheck

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
        //viewModel 로 response 관리. 연결 어댑터 diffUtil로 뱃지 노출
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

            "isPublic" -> {
                binding.privateSettingBtn.text = if (state) "이미지 게시 해지하기" else "이미지 게시하기"
            }
        }
    }

    private fun showDeclareDialog() {
        val declareArray = resources.getStringArray(R.array.declare_array)
        Log.e(TAG, "showDeclareDialog: $declareArray")
        val dialog = AlertDialog.Builder(context)
            .setTitle("신고")
            .setItems(R.array.declare_array) { _, p1 ->
                getDetailInfo(declareArray[p1])
            }
            .setNegativeButton("닫기") { _, _ ->
            }
        dialog.show()
    }

    private fun getDetailInfo(p1: String) {
        Log.e(TAG, "getDetailInfo: $p1")
        val dialogView = LayoutInflater.from(context).inflate(R.layout.item_dialog_declare, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("등록") { _, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    val response = viewModel.declarePost(
                        DeclareInfo(
                            post!!.id,
                            dialogView.findViewById<EditText>(R.id.declare_value).text.toString()
                        )
                    )
                    if (response.success) {
                        Toast.makeText(requireContext(), "신고가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    this@PostBottomSheetFragment.dismiss()
                }
            }
            .setNegativeButton("닫기") { _, _ ->

            }
        dialog.show()

    }
}