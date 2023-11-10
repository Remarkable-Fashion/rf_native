package com.lf.fashion.ui.home.userInfo.cloth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.UserDataStorePref
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.ClothPost
import com.lf.fashion.databinding.HomeBRecommendFragmentBinding
import com.lf.fashion.ui.common.CopyLink
import com.lf.fashion.ui.common.CreateDynamicLink
import com.lf.fashion.ui.common.cancelBtnBackStack
import com.lf.fashion.ui.home.ClothLikeClickListener
import com.lf.fashion.ui.home.userInfo.UserInfoViewModel
import com.lf.fashion.ui.home.adapter.LookBookRvAdapter
import com.lf.fashion.ui.globalFrag.bottomsheet.PostBottomSheetFragment
import com.lf.fashion.ui.common.showRequireLoginDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

/**
 * 메인 홈에서 유저 정보보기 -> 이 의상은 어때 버튼 클릭시 노출되는 프래그먼트입니다.
 */
@AndroidEntryPoint
class RecommendLooBookFragment : Fragment(R.layout.home_b_recommend_fragment),
    View.OnClickListener,
    AdapterView.OnItemSelectedListener,
    ClothLikeClickListener {
    private lateinit var binding: HomeBRecommendFragmentBinding
    private val viewModel: UserInfoViewModel by viewModels()
    private lateinit var userPref: UserDataStorePref
    private lateinit var lookBookRvAdapter: LookBookRvAdapter
    private var postId by Delegates.notNull<Int>()
    private var isOrderByInit by Delegates.notNull<Boolean>()
    private var isSpinnerInit by Delegates.notNull<Boolean>()
    private var isCategoryInit by Delegates.notNull<Boolean>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeBRecommendFragmentBinding.bind(view)
        userPref = UserDataStorePref(requireContext().applicationContext)

        isSpinnerInit = false // 옵션 값 초기화할때 observe 되어서 중복 다수 요청됨을 방지
        isOrderByInit = false
        isCategoryInit = false
        cancelBtnBackStack(binding.cancelBtn)
        spinnerSetting()

        arguments?.getBoolean("backStackClear")?.let {
            if (it) {
                findNavController().popBackStack(
                    R.id.registClothFragment,
                    true
                ) // RegistFragment를 back stack에서 제거
            }
        }
        postId = arguments?.get("postId") as Int
        binding.orderByBestBtn.isSelected = true // default 베스트 순
        binding.orderByBestBtn.setOnClickListener(this)
        binding.orderByRecentBtn.setOnClickListener(this)

        //todo usershare test
        //profile space 케밥 버튼
        lookBookRvAdapter = LookBookRvAdapter({ userId ->
            val dialog = PostBottomSheetFragment(userId = userId , userShareOnclick = {
                CreateDynamicLink(requireContext(), "recommend", postId)
            }){
                CopyLink().copyTextToClipboard(requireContext(),postId,"recommend")
            }
            dialog.show(parentFragmentManager, "bottom_sheet")
        }, this)

        //카테고리가 변할 때마다 새로 요청
        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            if (isCategoryInit) {
                requestLookBook()
            }
            isCategoryInit = true
        }

        //베스트 / 최신순 변할 때마다 새로 요청
        viewModel.orderByMode.observe(viewLifecycleOwner) { orderBy ->
            if (isOrderByInit) {
                requestLookBook()
            }
            isOrderByInit = true
        }
        requestLookBook()

        observeLookBookResponse()
        clothesRegButtonOnclick()
    }

    private fun requestLookBook() {
        viewModel.getTop3LookBook(
            postId,
            viewModel.selectedCategory.value ?: "All",
            viewModel.orderByMode.value ?: "Best"
        )
    }

    private fun observeLookBookResponse() {
        viewModel.lookBook.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val response = resource.value

                    binding.styleRecommendRv.apply {
                        adapter = lookBookRvAdapter.apply {
                            val nullOrEmpty = response.clothes.isNullOrEmpty()
                            binding.arrayEmptyText.isVisible = nullOrEmpty
                            binding.styleRecommendRv.isVisible = !nullOrEmpty
                            //topList.addAll(response.clothes)
                            submitList(response.clothes)
                        }

                    }
                }

                else -> {

                }
            }
        }
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
            if (userPref.loginCheck()) {
                findNavController().navigate(
                    R.id.action_recommendFragment_to_registClothFragment,
                    bundleOf("clothesPostId" to postId)
                )
            } else {
                showRequireLoginDialog()
            }
        }
    }

    override fun onClick(view: View?) {
        val singleClickableList = listOf(binding.orderByRecentBtn, binding.orderByBestBtn)
        singleClickableList.forEach { button ->
            button.isSelected = button == view
            if (button.isSelected) {
                viewModel.orderByMode.value =
                    if (button.text.toString() == "최신순") "Recent" else "Best"
                lookBookRvAdapter.updateUI(viewModel.orderByMode.value!!)

            }
        }
    }

    //spinner listener
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (isSpinnerInit) {
            viewModel.selectedCategory.value = parent.getItemAtPosition(position).toString()
        }
        isSpinnerInit = true
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun clothLikeBtnClicked(likeState: Boolean, clothes: ClothPost) {
        if (userPref.loginCheck()) {
            CoroutineScope(Dispatchers.IO).launch {
                val response =
                    viewModel.changeClotheLikeState(create = !likeState, clothes.clothesInfo.id!!)
                if (response is Resource.Success && response.value.success) {
                    val currentList = lookBookRvAdapter.currentList
                    val position = currentList.indexOf(clothes)

                    if (position != -1) {
                        lookBookRvAdapter.currentList[position].apply {
                            this.isFavorite = !(clothes.isFavorite ?: false)
                            val count = clothesInfo.count!!.favorites // 반전 및 카운트 업데이트
                            this.clothesInfo.count.favorites =
                                if (likeState) count!!.minus(1) else count!!.plus(1)
                        }
                        lookBookRvAdapter.notifyItemChanged(position, "FAVORITES_COUNT")
                    }
                }

            }
        } else {
            showRequireLoginDialog()
        }
    }
}
