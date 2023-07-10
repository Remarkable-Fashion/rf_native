package com.lf.fashion.ui.scrap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.ScrapFragmentBinding
import com.lf.fashion.ui.home.GridSpaceItemDecoration
import com.lf.fashion.ui.GridPhotoClickListener
import com.lf.fashion.ui.GridPostAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScrapFragment : Fragment(), GridPhotoClickListener {
    private lateinit var binding: ScrapFragmentBinding
    private val viewModel: ScrapViewModel by hiltNavGraphViewModels(R.id.navigation_scrap)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScrapFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.postResponse.observe(viewLifecycleOwner) { resources ->
            when (resources) {
                is Resource.Success -> {
                    val response = resources.value
                    Log.d(TAG, "ScrapFragment - onViewCreated RESPONSE: $response");
                    if (response.posts.isNotEmpty()) {
                        binding.scrapRv.visibility = View.VISIBLE
                        binding.arrayEmptyText.visibility = View.GONE

                        with(binding.scrapRv) {
                            adapter =
                                GridPostAdapter(3, this@ScrapFragment, scrapPage = true).apply {
                                    while (itemDecorationCount > 0) { // 기존 추가한 itemDecoration 을 모두 지워주지않으면 점점 쌓인다.
                                        removeItemDecorationAt(0)
                                    }
                                    addItemDecoration(GridSpaceItemDecoration(3, 6))
                                    this.submitList(response.posts)

                                }
                        }
                    } else {
                        binding.scrapRv.visibility = View.GONE
                        binding.arrayEmptyText.visibility = View.VISIBLE
                    }
                }
                is Resource.Failure -> {
                    if (resources.errorCode == 401) {
                        Toast.makeText(requireContext(), "로그인 후 이용가능합니다.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Log.e(TAG, "onViewCreated postList response Error: $resources ")
                    }

                }
                is Resource.Loading -> {

                }
            }

        }
    }

    override fun gridPhotoClicked(postIndex: Int) {
        Log.d(TAG, "ScrapFragment - gridPhotoClicked: grid 포토 클릭 $postIndex")
        // post list 에서 클릭한 포토의 포지션을 viewModel 에 저장
        viewModel.editClickedPostIndex(postIndex)
        findNavController().navigate(R.id.action_navigation_scrap_to_scrapVerticalFragment)
    }
}