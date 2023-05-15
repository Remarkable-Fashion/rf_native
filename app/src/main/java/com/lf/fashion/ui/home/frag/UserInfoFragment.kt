package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.databinding.HomeBUserInfoFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserInfoFragment : Fragment() {
    private lateinit var binding: HomeBUserInfoFragmentBinding
    private val viewModel : UserInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBUserInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recommendBtn.setOnClickListener{
            findNavController().navigate(R.id.action_userInfoFragment_to_recommendFragment)
        }
        cancelBtnBackStack(binding.cancelBtn)

        viewModel.getUserInfoAndStyle()
        viewModel.userInfo.observe(viewLifecycleOwner){
            Log.d(TAG, "UserInfoFragment - onViewCreated: $it");

        }

        //binding.clothesRv.adapter
    }
}