package com.lf.fashion.ui.home.userInfo.cloth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.ClothPost
import com.lf.fashion.databinding.HomeBRecommendFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.userInfo.UserInfoViewModel
import com.lf.fashion.ui.home.adapter.LookBookRvAdapter
import com.lf.fashion.ui.home.frag.PostBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인 홈에서 유저 정보보기 -> 이 의상은 어때 버튼 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class RecommendLooBookFragment : Fragment(R.layout.home_b_recommend_fragment), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: HomeBRecommendFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()
    private val topList = mutableListOf<ClothPost>()
    private var selectedCategory: MutableLiveData<String> = MutableLiveData()
    private var orderByMode : String = "Best"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBRecommendFragmentBinding.bind(view)

        cancelBtnBackStack(binding.cancelBtn)
        spinnerSetting()

        val postId = arguments?.get("postId") as Int
        binding.orderByBestBtn.isSelected = true // default 베스트 순
        binding.orderByBestBtn.setOnClickListener(this)
        binding.orderByRecentBtn.setOnClickListener(this)

        //profile space 케밥 버튼
        val lookBookRvAdapter = LookBookRvAdapter { userId->
            val dialog = PostBottomSheetFragment(userId = userId)
            dialog.show(parentFragmentManager, "bottom_sheet")
        }

        //카테고리가 변할 때마다 새로 요청
        selectedCategory.observe(viewLifecycleOwner){category ->
            viewModel.getTopLook(postId, category)
            viewModel.getLookBook(postId, category)
        }

        viewModel.topLook.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resource.Success -> {
                    Log.d(TAG, "RecommendLooBookFragment - onViewCreated: ${resources.value}");
                    topList.removeAll(topList)
                    topList.addAll(resources.value.clothes)
                }

                else -> {

                }
            }
        }

        viewModel.lookBook.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    binding.styleRecommendRv.apply {
                        adapter = lookBookRvAdapter.apply {
                            topList.addAll(response.clothes)
                            submitList(topList)
                        }

                    }
                }

                else -> {

                }
            }
        }

        clothesRegButtonOnclick()

    }

    private fun spinnerSetting() {
        val spinner = binding.spinner
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_array,
            R.layout.spinner_text_view
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun clothesRegButtonOnclick() {
        binding.registBtn.setOnClickListener {
            findNavController().navigate(R.id.action_recommendFragment_to_registClothFragment)

        }
    }

    override fun onClick(view: View?) {
        val singleClickableList = listOf(binding.orderByRecentBtn, binding.orderByBestBtn)
        singleClickableList.forEach { button ->
            button.isSelected = button == view
            orderByMode = if(button.text.toString() == "최신순") "Recent" else "Best"
        }
    }

    //spinner listener
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        selectedCategory.value = parent.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}