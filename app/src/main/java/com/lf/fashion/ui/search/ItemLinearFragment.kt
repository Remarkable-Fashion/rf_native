package com.lf.fashion.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.TAG
import com.lf.fashion.databinding.SearchItemListFragmentBinding
import com.lf.fashion.ui.home.UserInfoViewModel
import com.lf.fashion.ui.search.adapter.ItemRvAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemLinearFragment : Fragment() {
    private lateinit var binding: SearchItemListFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchItemListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserInfoAndStyle()
        viewModel.userInfo.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                Log.d(TAG, "ItemLinearFragment - onViewCreated: $it");
                with(binding.linearRv) {
                    adapter = ItemRvAdapter().apply {
                        submitList(it.clothesInfo)
                    }
                }
            }
        }
    }
}