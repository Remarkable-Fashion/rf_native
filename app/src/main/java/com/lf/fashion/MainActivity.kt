package com.lf.fashion

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.lf.fashion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        bottomNavigationView.itemIconTintList = null
        val navController =
            supportFragmentManager.findFragmentById(R.id.mainContainer)?.findNavController()
        navController?.let {
            bottomNavigationView.setupWithNavController(it)
            bottomNaviSetItemSelectedListener(it)
        }


        //bottomNavigationView.selectedItemId = selectedMenuItemId

        // BottomNavigationView의 메뉴 아이템 클릭 리스너 설정
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
