package com.lf.fashion.ui.search

import android.os.Bundle
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.lf.fashion.R
import com.lf.fashion.databinding.SearchItemFilterFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.frag.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * 홈 메인 상단의 필터 아이콘을 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class ItemFilterFragment : Fragment(R.layout.search_item_filter_fragment),View.OnClickListener {
    private lateinit var binding: SearchItemFilterFragmentBinding
    private val viewModel: FilterViewModel by viewModels()
    private val chipStyle = "default"
    private val priceMaxLimit = 500000
    private var minPrice = 0
    private var maxPrice = 50000
    override fun onResume() {
        viewModel.selectedGender?.let {
            if (it == "Male") { binding.genderManBtn.isSelected = true }
            else{ binding.genderWomanBtn.isSelected = true }
        }
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchItemFilterFragmentBinding.bind(view)
        //생성 후 다른 바텀 메뉴 이동시 다시 home menu 클릭시 selected 아이콘으로 변경 안되는 오류 해결하기 위해 수동 메뉴 checked 코드 추가
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val homeMenu = bottomNavigationView.menu.findItem(R.id.navigation_search)
        homeMenu.isChecked = true

        binding.priceMax = maxPrice // 초기값 5만원

        arguments?.get("searchResult")?.let {
            if (it as Boolean) {
                //searchResult 일 경우 스피너 visible
            }
        }
        binding.genderManBtn.setOnClickListener(this)
        binding.genderWomanBtn.setOnClickListener(this)

        val tableLayout = binding.paletteSpace.table
        for (i in 0 until tableLayout.childCount) {
            val tableRow = tableLayout.getChildAt(i) as TableRow
            for (j in 0 until tableRow.childCount) {
                val textView = tableRow.getChildAt(j) as TextView
                textView.setOnClickListener {
                    textView.isSelected = !textView.isSelected
                }
            }
        }


        cancelBtnBackStack(binding.cancelBtn)
        priceSeekbarListener()
    }

    private fun priceSeekbarListener() {
        binding.priceSeekbar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onRangeChanged(
                view: RangeSeekBar?,
                leftValue: Float,
                rightValue: Float,
                isFromUser: Boolean
            ) {
                val leftPrice = (priceMaxLimit / 100) * (leftValue.toInt())
                val rightPrice = (priceMaxLimit / 100) * (rightValue.toInt())
                binding.priceMin = leftPrice
                binding.priceMax = rightPrice
                minPrice = leftPrice
                maxPrice = rightPrice
            }

            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

        })
    }


    override fun onClick(v: View?) {
        val genderBtns = listOf(
            binding.genderManBtn,
            binding.genderWomanBtn
        )
        genderBtns.forEach { button ->
            button.isSelected = button == v
            if (button.isSelected) {
                viewModel.selectedGender = if (button.text.toString() == "MAN") "Male" else "Female"
            }
        }
    }
}