package com.lf.fashion.ui.mypage.setting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.databinding.MypageServicetermFragmentBinding
import com.lf.fashion.ui.common.cancelBtnBackStack
import com.lf.fashion.ui.common.getAssetsTextString

class ServiceTermFragment :Fragment(R.layout.mypage_serviceterm_fragment) {
    private lateinit var binding:MypageServicetermFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.hideNavi(true)
    }
    override fun onDestroy() {
        super.onDestroy()
        MainActivity.hideNavi(false)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MypageServicetermFragmentBinding.bind(view)

        binding.policyValue.text=
        getAssetsTextString(requireContext(),"service_term")
        cancelBtnBackStack(binding.cancelBtn)
    }

}