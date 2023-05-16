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
import com.lf.fashion.R
import com.lf.fashion.databinding.HomeBRecommendFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.UserInfoViewModel
import com.lf.fashion.ui.home.adapter.LookBookRvAdapter
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onClick(view: View?) {
        when (view) {
            binding.orderByBestBtn -> {
                // 첫 줄에서 isSelected 값이 변경되었기 때문에 recentBtn 에는 반대 값이 들어감
                // 두가지 정렬 옵션 중 한개만 선택 가능하도록 if 문 처리 ~
                binding.orderByBestBtn.isSelected = !binding.orderByBestBtn.isSelected
                if(binding.orderByBestBtn.isSelected) {
                    binding.orderByRecentBtn.isSelected = !binding.orderByBestBtn.isSelected
                }
            }
            binding.orderByRecentBtn -> {
                binding.orderByRecentBtn.isSelected = !binding.orderByRecentBtn.isSelected
                if(binding.orderByRecentBtn.isSelected) {
                    binding.orderByBestBtn.isSelected = !binding.orderByRecentBtn.isSelected
                }
            }
        }
    }

    //spinner listener
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}