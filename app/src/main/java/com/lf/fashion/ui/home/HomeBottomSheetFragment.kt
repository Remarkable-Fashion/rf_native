package com.lf.fashion.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lf.fashion.databinding.HomeBottomDialogItemBinding

class HomeBottomSheetFragment : BottomSheetDialogFragment(), View.OnClickListener {
    lateinit var binding: HomeBottomDialogItemBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBottomDialogItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomLayout.children.forEach { it.setOnClickListener(this) }
        binding.bottomLinear.children.forEach { it.setOnClickListener(this) }
    }

    override fun onClick(view: View?) {
        when(view){
            binding.bottomSheetLinkCopyBtn->{

            }
            binding.bottomSheetShareBtn->{

            }
            binding.bottomSheetScrapBtn->{

            }
            binding.noInterestBtn->{

            }
            binding.cancelFollowBtn->{

            }
            binding.blockBtn->{

            }
            binding.declareBtn->{

            }
        }
    }
}