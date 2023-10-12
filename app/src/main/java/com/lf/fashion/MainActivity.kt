package com.lf.fashion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.lf.fashion.data.common.PostFilterDataStore
import com.lf.fashion.data.common.SearchItemFilterDataStore
import com.lf.fashion.data.common.SearchLookFilterDataStore
import com.lf.fashion.databinding.ActivityMainBinding
import com.lf.fashion.ui.common.AppCustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var lookFilterDataStore: SearchLookFilterDataStore
    private lateinit var itemFilterDataStore: SearchItemFilterDataStore
    private lateinit var postFilterDataStore: PostFilterDataStore
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lookFilterDataStore = SearchLookFilterDataStore(this.applicationContext)
        itemFilterDataStore = SearchItemFilterDataStore(this.applicationContext)
        postFilterDataStore = PostFilterDataStore(this.applicationContext)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        bottomNavigationView.itemIconTintList = null
        val navController =
            supportFragmentManager.findFragmentById(R.id.mainContainer)?.findNavController()
        navController?.let {
            bottomNavigationView.setupWithNavController(it)
            bottomNaviSetItemSelectedListener(it)
        }

        // BottomNavigationView의 메뉴 아이템 다시 클릭할 경우(reselected)리스너 설정
        bottomNaviReselectedListener(navController)

        //딥링크 부분 .. 구현하다가 말았..
        Firebase.dynamicLinks.getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    Log.d(TAG, "MainActivity - onCreate: 호출된 딥링크 $deepLink")
                }

                // Handle the deep link. For example, open the linked
                // content, or apply promotional credit to the user's
                // account.
                // ...
            }
            .addOnFailureListener(this) { e -> Log.e(TAG, "onCreate: 다이나믹 링크 $e") }

        //딥링크 데이터 꺼내기
        val data =intent.data
        data?.let{
            val path = data.path
        }

        updateAvailabilityCheck()
    }

    override fun onDestroy() {
        super.onDestroy()
        CoroutineScope(Dispatchers.IO).launch {
            lookFilterDataStore.clearLookFilter()
            itemFilterDataStore.clearItemFilter()
            postFilterDataStore.clearMainFilter()
        }
    }
    private fun updateAvailabilityCheck() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE /*&& appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE)*/) {
                // 유효한 업데이트가 있을 때 스토어로 이동시키는 알림창 띄워주기
                AppCustomDialog("업데이트 가능한 버전이 존재합니다.", "업데이트를 위해 구글 플레이스토어로 이동하시겠습니까?") {
                    val storeIntent = Intent(Intent.ACTION_VIEW)
                    storeIntent.data = Uri.parse("market://details?id=com.lf.fashion")
                    startActivity(storeIntent)
                }.show(supportFragmentManager, "update_dialog")

            }
        }
    }
    companion object {
        private lateinit var binding: ActivityMainBinding
        fun hideNavi(state: Boolean) {
            if (::binding.isInitialized) {
                if (state) binding.bottomNavBar.visibility =
                    View.GONE else binding.bottomNavBar.visibility = View.VISIBLE
            }
        }

        fun bottomNaviSetItemSelectedListener(
            navController: NavController
        ) {
            binding.bottomNavBar.setOnItemSelectedListener { item ->
                //로그인 요청 이후에 페이지 이동 오류 방지
                val currentId = navController.currentDestination?.id
                if( currentId== R.id.loginFragment || currentId == R.id.mypage_fragment){
                     when (item.itemId) {
                         R.id.navigation_home -> {
                             navController.navigate(R.id.navigation_home)
                         }

                         R.id.navigation_mypage -> {
                             navController.navigate(R.id.navigation_mypage)
                         }

                         R.id.navigation_photo -> {
                             navController.navigate(R.id.navigation_photo)
                         }

                         R.id.navigation_scrap -> {
                             navController.navigate(R.id.navigation_scrap)
                         }

                         R.id.navigation_search -> {
                             navController.navigate(R.id.navigation_search)
                         }
                     }
                 }
                NavigationUI.onNavDestinationSelected(item, navController)

                return@setOnItemSelectedListener true
            }
        }

        fun bottomNaviReselectedListener(
            navController: NavController?
        ) {
            binding.bottomNavBar.setOnItemReselectedListener { item ->
                // 현재 선택된 메뉴 아이디
                val currentMenuItemId = binding.bottomNavBar.selectedItemId

                // 클릭된 메뉴 아이템이 현재 선택된 메뉴와 동일한 경우
                if (item.itemId == currentMenuItemId) {
                    Log.e(TAG, "onCreate: setOnItemReselectedListener")
                    when (item.itemId) {
                        R.id.navigation_search -> {
                            navController?.navigate(R.id.navigation_search)
                        }

                        R.id.navigation_mypage -> {
                            navController?.navigate(R.id.navigation_mypage)
                        }

                        R.id.navigation_photo -> {
                            navController?.navigate(R.id.navigation_photo)
                        }

                        R.id.navigation_home -> {
                            navController?.navigate(R.id.navigation_home)
                            /*  navController?.apply {
                                    popBackStack(R.id.navigation_home, false)
                                }*/
                        }
                        R.id.navigation_scrap ->{
                            navController?.navigate(R.id.navigation_scrap)
                        }
                    }
                }
                true
            }
        }
    }
}
