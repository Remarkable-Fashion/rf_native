package com.lf.fashion.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding : HomeFragmentBinding
    private var defaultRandomPhoto = true
    private val viewModel : HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }
    //TODO: 보고싶은 성별을 선택하는 다이얼로그 만들어야함
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


        //grid 모드 item 으로 변경
        binding.photoGridModeBtn.setOnClickListener {
            binding.photoGridModeBtn.text =if(binding.photoGridModeBtn.text =="1") "3" else "1"
        }

        val defaultPostAdapter = DefaultPostAdapter()
        val homeRecyclerView = binding.homeRecyclerView
        homeRecyclerView.adapter = defaultPostAdapter
        homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.postList.observe(viewLifecycleOwner){
            it?.let{
                Log.d(TAG, "response ${it}")
                defaultPostAdapter.submitList(it)
            }
        }
        Log.d(TAG, "onViewCreated: 1!!!")
        //    this.layoutManager = LinearLayoutManager(requireContext())
         //   this.adapter = defaultPostAdapter


    }

    private fun topMenuTextUiUpdate(boolean: Boolean){
        binding.appBarRandom.isSelected = boolean
        binding.appBarFollowing.isSelected = !boolean
    }
}