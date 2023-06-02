package com.lf.fashion.ui.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.databinding.MypageFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.home.adapter.GridPostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MyPageFragment : Fragment() {
    private lateinit var binding: MypageFragmentBinding
    private val viewModel : MyPageViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        runBlocking {
         launch {
             viewModel.getSavedLoginToken()
         }
        }
        /* Bottom dialog 에서도 사용하기 편하도록 viewModel 로 넣어서 observe 했지만 ,preferenceManager.accessToken.asLiveData().observe 해도 된다. */
        viewModel.savedLoginToken.observe(viewLifecycleOwner){
            if(it.isNullOrEmpty()){
                findNavController().navigate(R.id.action_navigation_mypage_to_loginFragment)
            }
        }
        binding = MypageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileEditBtn.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_mypage_to_profileEditFragment)
        }
        //바텀 다이얼로그 show
        binding.settingBtn.setOnClickListener {
            val dialog = SettingBottomSheetFragment(viewModel)
            dialog.show(parentFragmentManager, "setting_bottom_sheet")
        }

        viewModel.getPostList()
        viewModel.postList.observe(viewLifecycleOwner) { response ->
            with(binding.gridRv) { //grid layout
                adapter = GridPostAdapter(3).apply {
                    addItemDecoration(GridSpaceItemDecoration(3, 6))
                    submitList(response)
                }
            }
        }

    }
}
