package com.lf.fashion.ui.addPost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.TAG
import com.lf.fashion.databinding.PhotoImagePickerFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

class ImagePickerFragment : Fragment() {
    private lateinit var binding : PhotoImagePickerFragmentBinding
    private val viewModel: ImagePickerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PhotoImagePickerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonListener = View.OnClickListener {
            // Pass Uri list to fragment outside
            Log.d(TAG, "ImagePickerFragment - onCreateView: ${viewModel.getCheckedImageUriList()}");
            /* activity?.supportFragmentManager?.setFragmentResult(
                 URI_LIST_CHECKED,
                 bundleOf("uriList" to viewModel.getCheckedImageUriList())
             )*/
            // findNavController().navigateUp()
        }
        val adapter = ImageAdapter(viewModel)
        binding.recyclerviewImage.adapter = adapter

        viewModel.fetchImageItemList(requireContext())
        subscribeUi(adapter)

    }
    private fun subscribeUi(adapter: ImageAdapter) {
        viewModel.imageItemList.observe(viewLifecycleOwner) { imageItemList ->
            adapter.submitList(imageItemList)
        }
    }
}