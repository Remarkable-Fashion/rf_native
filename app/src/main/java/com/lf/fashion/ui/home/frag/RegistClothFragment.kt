package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.lf.fashion.data.response.RegClothes
import com.lf.fashion.databinding.HomeBRegistClothFragmentBinding
import com.lf.fashion.ui.addPost.adapter.AddPostClothesRvAdapter

class RegistClothFragment : Fragment(),View.OnClickListener {
    private lateinit var binding: HomeBRegistClothFragmentBinding
    private val regClothesList = mutableListOf<RegClothes>()
    private var selectedCategory: String? = null
    private val addClothesAdapter = AddPostClothesRvAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBRegistClothFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clothesDetailRv.adapter = addClothesAdapter
        registerCloth()
        detailValueLengthCounting()

    }
    private fun registerCloth() {
        binding.clothRegistForm.topLinear.children.forEach { it.setOnClickListener(this) }
        binding.regClothBtn.setOnClickListener {
            val nameValue = binding.clothRegistForm.nameValue.text.toString()
            val priceValue = binding.clothRegistForm.priceValue.text.toString()
            val colorValue = binding.clothRegistForm.colorValue.text.toString()
            val sizeValue = binding.clothRegistForm.sizeValue.text.toString()
            val urlValue = binding.clothRegistForm.urlValue.text.toString()

            if (nameValue.isNotEmpty() && priceValue.isNotEmpty() && colorValue.isNotEmpty() && sizeValue.isNotEmpty() && selectedCategory != null) {

                regClothesList.add(
                    RegClothes(
                        null,
                        selectedCategory!!,
                        nameValue,
                        priceValue,
                        colorValue,
                        sizeValue,
                        urlValue
                    )
                )
                addClothesAdapter.apply {
                    submitList(regClothesList)
                    notifyItemInserted(regClothesList.size-1)
                }

                // 요소들의 텍스트를 빈 값으로 설정
                binding.clothRegistForm.nameValue.text.clear()
                binding.clothRegistForm.priceValue.text.clear()
                binding.clothRegistForm.colorValue.text.clear()
                binding.clothRegistForm.sizeValue.text.clear()
                binding.clothRegistForm.urlValue.text.clear()

            } else if (selectedCategory == null) {
                Toast.makeText(requireContext(), "의상 카테고리를 선택해주세요!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "의상에 관한 정보를 모두 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }

    }
    override fun onClick(v: View?) {
        val categoryButtons = listOf(
            binding.clothRegistForm.outerBtn,
            binding.clothRegistForm.topBtn,
            binding.clothRegistForm.bottomBtn,
            binding.clothRegistForm.shoesBtn,
            binding.clothRegistForm.accBtn
        )
        categoryButtons.forEach { button ->
            button.isSelected = button == v
            if (button.isSelected) {
                selectedCategory = button.text.toString()
            }
        }
    }
    private fun detailValueLengthCounting() {
        binding.detailValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val count = s.toString().count()
                binding.textCounter.text = "$count/50"
            }

        })
    }


}