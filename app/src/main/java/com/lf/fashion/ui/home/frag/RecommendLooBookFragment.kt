package com.lf.fashion.ui.home.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.Cloth
import com.lf.fashion.data.response.ClothPost
import com.lf.fashion.data.response.RecommendCloth
import com.lf.fashion.databinding.HomeBRecommendFragmentBinding
import com.lf.fashion.ui.PrefCheckService
import com.lf.fashion.ui.cancelBtnBackStack
import com.lf.fashion.ui.home.UserInfoViewModel
import com.lf.fashion.ui.home.adapter.LookBookRvAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인 홈에서 유저 정보보기 -> 이 의상은 어때 버튼 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class RecommendLooBookFragment : Fragment(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var binding: HomeBRecommendFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()
    private val topList = mutableListOf<ClothPost>()
  /*  private lateinit var userPref: PreferenceManager
    private lateinit var prefCheckService: PrefCheckService*/
    private val lookBookRvAdapter = LookBookRvAdapter()
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
      /*  userPref = PreferenceManager(requireContext().applicationContext)
        prefCheckService = PrefCheckService(userPref)*/
        cancelBtnBackStack(binding.cancelBtn)
//        lookBookRvAdapter = LookBookRvAdapter(requireContext().applicationContext)
        val postId = arguments?.get("postId") as Int

        binding.orderByBestBtn.setOnClickListener(this)
        binding.orderByRecentBtn.setOnClickListener(this)

        viewModel.getTopLook(postId,"All")
        viewModel.getLookBook(postId, "All")

        viewModel.topLook.observe(viewLifecycleOwner){resources->
            when (resources) {
                is Resource.Success -> {
                    Log.d(TAG, "RecommendLooBookFragment - onViewCreated: ${resources.value}");
                    topList.removeAll(topList)
                    topList.addAll(resources.value.clothes)
                }
                else -> {

                }
            }

        }
        viewModel.lookBook.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value
                    binding.styleRecommendRv.apply {
                        adapter = lookBookRvAdapter.apply {
                            topList.addAll(response.clothes)
                            submitList(topList)
                        }

                    }
                }
                else -> {

                }
            }
        }

        spinnerSetting()
        clothesRegButtonOnclick()
    }

    private fun spinnerSetting() {
        val spinner = binding.spinner
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.spinner_array,
            R.layout.spinner_text_view
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun clothesRegButtonOnclick() {
        binding.registBtn.setOnClickListener {
            findNavController().navigate(R.id.action_recommendFragment_to_registClothFragment)

        }
    }

    override fun onClick(view: View?) {
        val singleClickableList = listOf(binding.orderByRecentBtn, binding.orderByBestBtn)
        singleClickableList.forEach { button ->
            button.isSelected = button == view
        }

    }

    //spinner listener
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}