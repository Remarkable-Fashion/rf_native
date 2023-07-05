package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.databinding.MypageFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


//TODO DEV 객체로 옮기고 나서 api 두개 사용하는 것으로 바뀜 ! binding 다시 해줘야한다 ~
@AndroidEntryPoint
class MyPageFragment : Fragment(), GridPhotoClickListener {
    private lateinit var binding: MypageFragmentBinding
    private val viewModel: MyPageViewModel by viewModels()
    private lateinit var userPref : PreferenceManager
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
         //Bottom dialog 에서도 사용하기 편하도록 viewModel 로 넣어서 observe 했지만 ,preferenceManager.accessToken.asLiveData().observe 해도 된다.

        binding = MypageFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.savedLoginToken.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_navigation_mypage_to_loginFragment)
            }else{
                viewModel.getPostList()
                with(binding.gridRv) { //grid layout
                    adapter = GridPostAdapter(3, this@MyPageFragment,null).apply {
                        viewModel.postList.observe(viewLifecycleOwner) { response ->
                            while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                                removeItemDecorationAt(0)
                            }
                            addItemDecoration(GridSpaceItemDecoration(3, 6))
                            submitList(response)
                        }
                    }
                }
            }
        }

        binding.profileEditBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mypage_to_profileEditFragment)
        }
        //바텀 다이얼로그 show
        binding.settingBtn.setOnClickListener {
            val dialog = SettingBottomSheetFragment(viewModel)
            dialog.show(parentFragmentManager, "setting_bottom_sheet")
        }



    }

    override fun gridPhotoClicked(postIndex:Int) {
        //grid 포토 클릭시!!
    }
}
