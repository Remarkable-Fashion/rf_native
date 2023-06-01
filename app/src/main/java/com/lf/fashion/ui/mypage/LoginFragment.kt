package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.common.PreferenceManager
import com.lf.fashion.databinding.LoginFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private lateinit var userPreferences: PreferenceManager
    private val viewModel : MyPageViewModel by viewModels()

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
        userPreferences = PreferenceManager(requireContext().applicationContext)

        binding.kakaoLoginBackground.setOnClickListener {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.d(TAG, "MyPageFragment - onViewCreated: 카카오톡 간편 로그인 실패 : $error");
                    if (error.message == "KakaoTalk not installed") {
                        kakaoLoginWithAccount()
                    }
                } else if (token != null) {
                    Log.d(TAG, "MyPageFragment - onViewCreated: 카카오톡 간편 로그인 성공 토큰 : $token")
                    runBlocking {
                        launch {
                            requestJWTToken(token)
                        }
                    }
                    getUserInfo()
                }
            }
        }
    }

    private fun kakaoLoginWithAccount() {
        UserApiClient.instance.loginWithKakaoAccount(requireContext()) { token, error ->
            token?.let {
                requestJWTToken(token)
            /*   runBlocking{
                    launch {
                        val response = viewModel.getJWT(token.accessToken)
                        if(response.success.toBoolean()){
                            Log.d(TAG, "LoginFragment - kakaoLoginWithAccount: success!!! ");
                            delay(1000)
                            findNavController().navigate(R.id.action_loginFragment_to_navigation_mypage)
                        }

                    }
                }*/
//            getUserInfo()
            }
        }
    }
    private fun requestJWTToken(token : OAuthToken){
        runBlocking{
            launch {
                val response = viewModel.getJWT(token.accessToken)
                if(response.success.toBoolean()){
                    Log.d(TAG, "LoginFragment - kakaoLoginWithAccount: success!!! ");
                    delay(1000)
                    findNavController().navigate(R.id.action_loginFragment_to_navigation_mypage)
                }

            }
        }
    }
private fun getUserInfo(){
    UserApiClient.instance.me { user, error ->
        if(error!=null){
            Log.d(TAG, "MyPageFragment - getUserInfo: 정보요청 실패");
        }else if(user!=null){
            Log.d(TAG, "MyPageFragment - getUserInfo: ${user.kakaoAccount?.email}");
            Log.d(TAG, "MyPageFragment - getUserInfo: ${user.kakaoAccount?.profile?.nickname}");
        }
    }
}
}
