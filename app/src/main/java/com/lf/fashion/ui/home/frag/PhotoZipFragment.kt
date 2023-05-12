package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.databinding.HomeBPhotoZipFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.home.HomeViewModel
import com.lf.fashion.ui.home.adapter.GridPostAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoZipFragment : Fragment() {
    lateinit var binding: HomeBPhotoZipFragmentBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBPhotoZipFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.postList.observe(viewLifecycleOwner) { response ->
            with(binding.gridRv) { //grid layout
                adapter = GridPostAdapter().apply {
                    addItemDecoration(GridSpaceItemDecoration(3, 6))
                    submitList(response)
                }
            }
        }
    }
}