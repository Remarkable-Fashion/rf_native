package com.lf.fashion.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lf.fashion.TAG
import com.lf.fashion.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {
    private lateinit var binding : HomeFragmentBinding
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
        //TODO: 보고싶은 성별을 선택하는 다이얼로그 ~
        Log.d(TAG, "HomeFragment - onViewCreated:    ");
    }
}