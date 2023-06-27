package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private lateinit var userPreferences: PreferenceManager
    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var keyHash = Utility.getKeyHash(requireContext())
        Log.d(TAG, "LoginFragment - key: $keyHash");
        userPreferences = PreferenceManager(requireContext().applicationContext)

        binding.kakaoLoginBackground.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.d(TAG, "MyPageFragment - onViewCreated: 카카오톡 간편 로그인 실패 : $error");
                    if (error.message == "KakaoTalk not installed") {
                        kakaoLoginWithAccount()
                    }else{
                        binding.progressBar.visibility = View.GONE
                        //alert 으로 오류 띄우는데, 나중에 배포시에는 오류코드로 바꾸거나 지워야합니다 ~!
                        AlertDialog.Builder(requireContext()).apply {
                            setMessage("${error.message}")
                        }.show()
                    }
                } else if (token != null) {
                    Log.d(TAG, "MyPageFragment - onViewCreated: 카카오톡 간편 로그인 성공 토큰 : $token")

                    requestJWTToken(token)
                }
            }
        }
    }

    private fun kakaoLoginWithAccount() {
        UserApiClient.instance.loginWithKakaoAccount(requireContext()) { token, _ ->
            token?.let {
                requestJWTToken(token)
            }
        }
    }
/* 응답이 느려짐.. 이유 모름 ,., */
    private fun requestJWTToken(token: OAuthToken) {
        runBlocking {
            launch {
                val response = viewModel.getJWT(token.accessToken)
                response.let { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            if (resource.value.success.toBoolean()) {
                          //      showLoading(requireActivity(),false)
                                Log.d(TAG, "LoginFragment - requestJWTToken: ");
                                findNavController().navigate(R.id.action_loginFragment_to_navigation_mypage)
                            }
                            binding.progressBar.visibility = View.GONE
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Failure -> {
                            binding.progressBar.visibility = View.GONE
                        }
                    }

                }
            }
        }
    }
   /* fun showLoading(activity: Activity, isShow: Boolean) {
        if (isShow) {
            val linear = LinearLayout(activity)
            linear.tag = "MyProgressBar"
            linear.gravity = Gravity.CENTER
            linear.setBackgroundColor(0x33000000)
            val progressBar = ProgressBar(activity)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            progressBar.layoutParams = layoutParams
            linear.addView(progressBar)
            linear.setOnClickListener { *//*클릭방지*//* }
            val rootView = activity.findViewById<FrameLayout>(android.R.id.content)
            rootView.addView(linear)
        } else {
            val rootView = activity.findViewById<FrameLayout>(android.R.id.content)
            val linear = rootView.findViewWithTag<LinearLayout>("MyProgressBar")
            if (linear != null) {
                rootView.removeView(linear)
            }
        }
    }*/

    private fun getUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.d(TAG, "MyPageFragment - getUserInfo: 정보요청 실패");
            } else if (user != null) {
                Log.d(TAG, "MyPageFragment - getUserInfo: ${user.kakaoAccount?.email}");
                Log.d(TAG, "MyPageFragment - getUserInfo: ${user.kakaoAccount?.profile?.nickname}");
            }
        }
    }
}
