package com.lf.fashion.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.SearchItemFilterDataStore
import com.lf.fashion.databinding.SearchItemFilterFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.frag.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 홈 메인 상단의 필터 아이콘을 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class ItemFilterFragment : Fragment(R.layout.search_item_filter_fragment), View.OnClickListener {
    private lateinit var binding: SearchItemFilterFragmentBinding
    private val viewModel: FilterViewModel by viewModels()
    private val priceMaxLimit = 500000
    private var minPriceDefault = 0
    private var maxPriceDefault = 50000
    private lateinit var itemFilterDataStore: SearchItemFilterDataStore
    override fun onResume() {
        viewModel.selectedGender?.let {
            if (it == "Male") {
                binding.genderManBtn.isSelected = true
            } else {
                binding.genderWomanBtn.isSelected = true
            }
        }
        viewModel.minPrice?.let {
            binding.priceMin = it
        }
        viewModel.maxPrice?.let {
            binding.priceMax = it
        }
        selectedColorBinding(viewModel.selectedColor)
        super.onResume()
    }

    private fun selectedColorBinding(savedColor:MutableList<String>) {
        val tableLayout = binding.paletteSpace.table
        if (savedColor.isNotEmpty()) {
            for (i in 0 until tableLayout.childCount) {
                val tableRow = tableLayout.getChildAt(i) as TableRow
                for (j in 0 until tableRow.childCount) {
                    val textView = tableRow.getChildAt(j) as TextView
                    textView.isSelected = textView.text.toString() in savedColor
                }
            }
        }else{
            for (i in 0 until tableLayout.childCount) {
                val tableRow = tableLayout.getChildAt(i) as TableRow
                for (j in 0 until tableRow.childCount) {
                    val textView = tableRow.getChildAt(j) as TextView
                    textView.isSelected = false
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchItemFilterFragmentBinding.bind(view)
        itemFilterDataStore = SearchItemFilterDataStore(requireContext().applicationContext)
        if (viewModel.maxPrice == null) {
            binding.priceMax = maxPriceDefault // 초기값 5만원
        }
        arguments?.get("searchResult")?.let {
            if (it as Boolean) {
                //searchResult 일 경우 스피너 visible
            }
        }
        binding.genderManBtn.setOnClickListener(this)
        binding.genderWomanBtn.setOnClickListener(this)

        //color 선택값 selected 처리 / viewModel 에 담기
        val tableLayout = binding.paletteSpace.table
        for (i in 0 until tableLayout.childCount) {
            val tableRow = tableLayout.getChildAt(i) as TableRow
            for (j in 0 until tableRow.childCount) {
                val textView = tableRow.getChildAt(j) as TextView
                textView.setOnClickListener {
                    textView.isSelected = !textView.isSelected

                    if (textView.isSelected) {
                        viewModel.selectedColor.add(textView.text.toString())
                    } else {
                        viewModel.selectedColor.remove(textView.text.toString())
                    }
                }
            }
        }


        cancelBtnBackStack(binding.cancelBtn)
        priceSeekbarListener()
        clearItemFilter()
        saveItemFilter()
        exposeSavedValue()
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
                viewModel.minPrice = leftPrice
                viewModel.maxPrice = rightPrice
                minPriceDefault = leftPrice
                maxPriceDefault = rightPrice
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

    private fun clearItemFilter() {
        binding.clearBtn.setOnClickListener {
            binding.genderManBtn.isSelected =false
            binding.genderWomanBtn.isSelected =false
            binding.priceMin = 0
            binding.priceMax = 50000
            binding.priceSeekbar.setProgress(0f,10f)
            viewModel.clearAll()
            selectedColorBinding(viewModel.selectedColor) //비어있는 mutableList 보내기
            CoroutineScope(Dispatchers.IO).launch {
                itemFilterDataStore.clearItemFilter()
            }
        }
    }

    private fun saveItemFilter() {
        binding.submitBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                itemFilterDataStore.saveItemFilterInstance(
                    viewModel.selectedGender,
                    viewModel.minPrice,
                    viewModel.maxPrice,
                    viewModel.selectedColor.joinToString(",")
                )
            }
            Toast.makeText(requireContext(),"필터가 저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun exposeSavedValue() {
        CoroutineScope(Dispatchers.Main).launch{
            with(itemFilterDataStore){
                itemGender.first()?.let {
                    if (it == "Male") {
                        binding.genderManBtn.isSelected = true
                    } else if (it == "Female") {
                        binding.genderWomanBtn.isSelected = true
                    }
                }
                val minPriceValue = minPrice.first()?:0
                val maxPriceValue = maxPrice.first()?:maxPriceDefault
                binding.priceMin = minPriceValue
                binding.priceMax = maxPriceValue
                val minPercentage = (minPriceValue.toFloat()/priceMaxLimit)*100
                val maxPercentage = (maxPriceValue.toFloat()/priceMaxLimit)*100
                binding.priceSeekbar.setProgress(minPercentage,maxPercentage)

                color.first()?.let{
                    val savedColorList = it.split(",").toMutableList()
                    selectedColorBinding(savedColorList)
                }
            }
        }
    }
}