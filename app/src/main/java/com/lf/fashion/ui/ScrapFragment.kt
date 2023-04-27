package com.lf.fashion.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lf.fashion.databinding.ScrapFragmentBinding

class ScrapFragment : Fragment() {
    private lateinit var binding : ScrapFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScrapFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }
}