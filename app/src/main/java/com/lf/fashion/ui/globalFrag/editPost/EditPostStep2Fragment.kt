package com.lf.fashion.ui.globalFrag.editPost

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.lf.fashion.R
import com.lf.fashion.data.model.Posts
import com.lf.fashion.databinding.EditPostStep2FragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostStep2Fragment : Fragment(R.layout.edit_post_step2_fragment) {
    private lateinit var binding : EditPostStep2FragmentBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditPostStep2FragmentBinding.bind(view)

        val post = arguments?.get("post") as Posts

    }
}