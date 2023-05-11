package com.lf.fashion.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lf.fashion.TAG
import com.lf.fashion.databinding.HomeBottomDialogItemBinding

class HomeBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var binding : HomeBottomDialogItemBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeBottomDialogItemBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.noInterestBtn.setOnClickListener {
            Log.d(TAG, "BottomSheetFragment - onViewCreated: ");
        }
    }
}