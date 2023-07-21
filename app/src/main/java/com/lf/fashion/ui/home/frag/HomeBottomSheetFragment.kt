package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.databinding.HomeBottomDialogItemBinding
import com.lf.fashion.ui.PrefCheckService

/**
 * 공유 버튼 클릭시 노출되는 바텀 다이얼로그 시트입니다
 */
class HomeBottomSheetFragment : BottomSheetDialogFragment(), View.OnClickListener {
    lateinit var binding: HomeBottomDialogItemBinding
    lateinit var userPref : PreferenceManager
    lateinit var prefCheckService: PrefCheckService

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
    }

    private fun loginUserUi() {
        if (prefCheckService.loginCheck()) {
            binding.cancelFollowBtn.isVisible = true
            binding.blockBtn.isVisible = true
        } else {
            binding.cancelFollowBtn.isVisible = false
            binding.blockBtn.isVisible = false
        }
    }

    override fun onClick(view: View?) {
        when(view){
            binding.bottomSheetLinkCopyBtn->{

            }
            binding.bottomSheetShareBtn->{

            }
            binding.bottomSheetScrapBtn->{

            }
            binding.noInterestBtn->{

            }
            binding.cancelFollowBtn->{

            }
            binding.blockBtn->{

            }
            binding.declareBtn->{

            }
        }
    }
}