package com.lf.fashion.ui.home.frag

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.lf.fashion.TAG
import com.lf.fashion.databinding.HomeBRecommendFragmentBinding
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.UserInfoViewModel
import com.lf.fashion.ui.home.adapter.LookBookRvAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendLooBookFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: HomeBRecommendFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBRecommendFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancelBtnBackStack(binding.cancelBtn)

        binding.orderByBestBtn.setOnClickListener(this)
        binding.orderByRecentBtn.setOnClickListener(this)


        viewModel.getLookBook()
        viewModel.lookBook.observe(viewLifecycleOwner){
            Log.d(TAG, "RecommendLooBookFragment - onViewCreated: $it")
             binding.styleRecommendRv.apply {
                 adapter = LookBookRvAdapter().apply {
                     submitList(it)
                 }

            }
        }
    }

    override fun onClick(view: View?) {
        Log.d(TAG, "RecommendLooBookFragment - onClick: $view");
        when (view) {
            binding.orderByBestBtn -> {
                // 첫 줄에서 isSelected 값이 변경되었기 때문에 recentBtn 에는 반대 값이 들어감
                binding.orderByBestBtn.isSelected = !binding.orderByBestBtn.isSelected
                binding.orderByRecentBtn.isSelected = !binding.orderByBestBtn.isSelected
            }
            binding.orderByRecentBtn -> {
                binding.orderByRecentBtn.isSelected = !binding.orderByRecentBtn.isSelected
                binding.orderByBestBtn.isSelected = !binding.orderByRecentBtn.isSelected

            }
        }
    }
}