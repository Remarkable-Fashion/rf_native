package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.databinding.MypageSettingBottomDialogBinding
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 마이페이지 설정 버튼 클릭시 노출되는 바텀 다이얼로그 시트입니다
 * myPageFragment 와 viewModel을 공유하여 login 여부를 체크해야함 (파라미터로 viewModel 받는 이유)
 */
class SettingBottomSheetFragment(private val viewModel: MyPageViewModel) :
    BottomSheetDialogFragment(), View.OnClickListener {
    lateinit var binding: MypageSettingBottomDialogBinding
    //private lateinit var userPref: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MypageSettingBottomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //userPref = PreferenceManager(requireContext().applicationContext)

        binding.bottomLayout.children.forEach { it.setOnClickListener(this) }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.profileEditBtn -> {

            }
            binding.alertSettingBtn -> {

            }
            binding.service -> {

            }
            binding.personalInfo -> {

            }
            binding.logoutBtn -> {
                viewModel.clearSavedLoginToken()

                /*runBlocking {
                    launch {
                        userPref.clearAccessTokenAndId()
                    }
                }*/
                this@SettingBottomSheetFragment.dismiss()
            }
            binding.Withdrawal -> {

            }

        }
    }
}