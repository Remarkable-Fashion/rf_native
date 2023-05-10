package com.lf.fashion.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lf.fashion.databinding.HomePhotoFilterFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack

class FilterFragment : Fragment() {
    private lateinit var binding : HomePhotoFilterFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomePhotoFilterFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.genderManBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }
        binding.genderWomanBtn.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        cancelBtnBackStack(binding.cancelBtn)
    }
}