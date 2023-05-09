package com.lf.fashion.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.lf.fashion.TAG
import com.lf.fashion.databinding.PhotoDetailFragmentBinding

class PhotoDetailFragment : Fragment() {
    private lateinit var binding : PhotoDetailFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PhotoDetailFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "PhotoDetailFragment - onViewCreated: ${arguments?.get("imageUrl").toString()}")
        val photoUrl = arguments?.get("imageUrl").toString()
        binding.photoUrl = photoUrl
    }
}