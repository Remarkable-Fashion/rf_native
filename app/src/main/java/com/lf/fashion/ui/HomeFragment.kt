package com.lf.fashion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {
    private lateinit var binding : HomeFragmentBinding
    private var defaultRandomPhoto = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상단 메뉴 - 랜덤/팔로잉 텍스트 선택 시 text color 변경 로직
        binding.appBarRandom.isSelected = defaultRandomPhoto
        binding.appBarFollowing.setOnClickListener {
            defaultRandomPhoto = false
            topMenuTextUiUpdate(defaultRandomPhoto)
        }
        binding.topRandomMenuLayer.setOnClickListener{
            defaultRandomPhoto = true
            topMenuTextUiUpdate(defaultRandomPhoto)
        }



        //TODO: 보고싶은 성별을 선택하는 다이얼로그 ~

    }

    private fun topMenuTextUiUpdate(boolean: Boolean){
        binding.appBarRandom.isSelected = boolean
        binding.appBarFollowing.isSelected = !boolean
    }
}