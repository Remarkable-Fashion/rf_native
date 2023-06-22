package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.databinding.HomeBRecommendFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.UserInfoViewModel
import com.lf.fashion.ui.home.adapter.LookBookRvAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인 홈에서 유저 정보보기 -> 이 의상은 어때 버튼 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class RecommendLooBookFragment : Fragment(), View.OnClickListener , AdapterView.OnItemSelectedListener {
    private lateinit var binding: HomeBRecommendFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBRecommendFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelBtnBackStack(binding.cancelBtn)

        binding.orderByBestBtn.setOnClickListener(this)
        binding.orderByRecentBtn.setOnClickListener(this)

        viewModel.getLookBook()
        viewModel.lookBook.observe(viewLifecycleOwner) {
            binding.styleRecommendRv.apply {
                adapter = LookBookRvAdapter().apply {
                    submitList(it)
                }

            }
        }

        spinnerSetting()
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
    private fun clothesRegButtonOnclick(){
        binding.registBtn.setOnClickListener {
            findNavController().navigate(R.id.action_recommendFragment_to_registClothFragment)

        }
    }
    override fun onClick(view: View?) {
        val singleClickableList = listOf(binding.orderByRecentBtn,binding.orderByBestBtn)
        singleClickableList.forEach { button->
            button.isSelected = button == view
        }

    }

    //spinner listener
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}