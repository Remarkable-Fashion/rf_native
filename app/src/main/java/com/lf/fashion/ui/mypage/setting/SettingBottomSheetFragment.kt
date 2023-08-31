package com.lf.fashion.ui.mypage.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lf.fashion.R
import com.lf.fashion.data.model.MyInfo
import com.lf.fashion.databinding.MypageSettingBottomDialogBinding
import com.lf.fashion.ui.mypage.MyPageViewModel

/**
 * 마이페이지 설정 버튼 클릭시 노출되는 바텀 다이얼로그 시트입니다
 * myPageFragment 와 viewModel을 공유하여 login 여부를 체크해야함 (파라미터로 viewModel 받는 이유)
 */
class SettingBottomSheetFragment(private val viewModel: MyPageViewModel) :
    BottomSheetDialogFragment(), View.OnClickListener {
    lateinit var binding: MypageSettingBottomDialogBinding
    private lateinit var globalMyInfo: MyInfo

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
        viewModel.myInfo.observe(viewLifecycleOwner) { myInfo ->
            myInfo?.let {
                globalMyInfo = myInfo
            }
        }
        binding.bottomLayout.children.forEach { it.setOnClickListener(this) }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.profileEditBtn -> {
                if (!::globalMyInfo.isInitialized) return

                findNavController().navigate(
                    R.id.action_navigation_mypage_to_profileEditFragment,
                    bundleOf("myInfo" to globalMyInfo)
                )
                this@SettingBottomSheetFragment.dismiss()
            }

            binding.alertSettingBtn -> {
                findNavController().navigate(
                    R.id.action_navigation_mypage_to_userAlertSettingFragment
                )
                this@SettingBottomSheetFragment.dismiss()
            }

            binding.termsOfService -> {
                findNavController().navigate(R.id.action_navigation_mypage_to_serviceTermFragment)
                this@SettingBottomSheetFragment.dismiss()
            }

            binding.privacyPolicy -> {
                findNavController().navigate(
                    R.id.action_navigation_mypage_to_privacyPolicyFragment
                )
                this@SettingBottomSheetFragment.dismiss()
            }

            binding.logoutBtn -> {
                viewModel.clearSavedLoginToken()
                this@SettingBottomSheetFragment.dismiss()
            }

            binding.Withdrawal -> {

            }

        }
    }
}