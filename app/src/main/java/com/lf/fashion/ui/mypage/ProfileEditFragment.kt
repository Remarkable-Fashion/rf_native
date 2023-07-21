package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.user.UserApi
import com.kakao.sdk.user.UserApiClient
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.response.ImageUrl
import com.lf.fashion.data.response.MyInfo
import com.lf.fashion.databinding.MypageProfileFragmentBinding
import com.lf.fashion.ui.PrefCheckService
import com.lf.fashion.ui.addTextChangeListener
import com.lf.fashion.ui.addTextLengthCounter
import com.lf.fashion.ui.cancelBtnBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {
    private lateinit var binding: MypageProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MypageProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cancelBtnBackStack(binding.backBtn)

        val myInfo = arguments?.get("myInfo") as MyInfo
        binding.myInfo = myInfo

        UserApiClient.instance.me { user, error ->
            if (error != null) {

            } else if (user != null) {
                Log.d(TAG, "ProfileEditFragment - onViewCreated: ${user.kakaoAccount?.email}")
                binding.emailAccount.text = user.kakaoAccount?.email.toString()
            }
        }

        genderListener(myInfo)
        addTextLengthCounter(binding.introduceValue, binding.textCounter, 50)

       // 값 변경시 완료 버튼 활성화
        addTextChangeListener(
            listOf(
                binding.nameValue,
                binding.phoneValue,
                binding.heightValue,
                binding.weightValue
            )
        ) { changed ->
            binding.submitBtn.isSelected = changed
        }

        binding.submitBtn.setOnClickListener {

        }
    }

    private fun genderListener(myInfo: MyInfo) {
        if (myInfo.profile.sex == "Male") {
            genderBtnSelectUi(Male = true)
        } else {
            genderBtnSelectUi(Male = false)
        }

        binding.genderInclude.genderManBtn.setOnClickListener {
            genderBtnSelectUi(Male = true)
        }
        binding.genderInclude.genderWomanBtn.setOnClickListener {
            genderBtnSelectUi(Male = false)
        }
    }

    private fun genderBtnSelectUi(Male: Boolean) {
        val manBtn = binding.genderInclude.genderManBtn
        val womanBtn = binding.genderInclude.genderWomanBtn

        if (Male) {
            manBtn.isSelected = true
            womanBtn.isSelected = false
        } else {
            manBtn.isSelected = false
            womanBtn.isSelected = true
        }
    }
}
