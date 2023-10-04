package com.lf.fashion.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.databinding.LoginFragmentBinding
import com.lf.fashion.ui.common.AppCustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.internal.wait


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var binding: LoginFragmentBinding
    private val viewModel: MyPageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginFragmentBinding.bind(view)

        binding.kakaoLoginBackground.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.d(TAG, "MyPageFragment - onViewCreated: 카카오톡 간편 로그인 실패 : $error");
                    if (error.message == "KakaoTalk not installed") {
                        kakaoLoginWithAccount()
                    } else {
                        binding.progressBar.visibility = View.GONE
                        //TODO alert 으로 오류 띄우는데, 나중에 배포시에는 오류코드로 바꾸거나 지워야합니다 ~!
                        AppCustomDialog("로그인 오류\n${error.message}").show(
                            parentFragmentManager,
                            "login_error"
                        )
                        /*  AlertDialog.Builder(requireContext()).apply {
                              setMessage("${error.message}")
                          }.show()*/
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
        CoroutineScope(Dispatchers.IO).launch {
            //fcm token get
            var fcmToken: String? = null
            val fcmTask = FirebaseMessaging.getInstance().token/*.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("dd", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new FCM registration token
                fcmToken = task.result
            })*/
            try {
                // Wait for the FCM token to be retrieved
                fcmToken = fcmTask.await()
            } catch (e: Exception) {
                Log.w("dd", "Fetching FCM registration token failed", e)
                // Handle the error if token retrieval fails
            }
            fcmToken?.let {
                val response = viewModel.getJWT(token.accessToken, it)
                response.let { resource ->
                    withContext(Dispatchers.Main) {
                        when (resource) {
                            is Resource.Success -> {
                                if (resource.value.success) {
                                    //      showLoading(requireActivity(),false)
                                    Log.d(TAG, "LoginFragment - requestJWTToken: ");
                                    findNavController().navigate(R.id.navigation_mypage)
                                }
                                binding.progressBar.visibility = View.GONE
                            }

                            is Resource.Loading -> {

                            }

                            is Resource.Failure ->
                                binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }



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
