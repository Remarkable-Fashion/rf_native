package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lf.fashion.R
import com.lf.fashion.databinding.MypageUserAlertSettingFragmentBinding

class UserAlertSettingFragment :Fragment(R.layout.mypage_user_alert_setting_fragment){
    private lateinit var binding : MypageUserAlertSettingFragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MypageUserAlertSettingFragmentBinding.bind(view)


    }
}