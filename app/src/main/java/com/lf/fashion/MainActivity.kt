package com.lf.fashion

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.dynamiclinks.DynamicLink.*
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.lf.fashion.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


//TODO : splash 이미지

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private lateinit var binding: ActivityMainBinding
        fun hideNavi(state: Boolean) {
            if (::binding.isInitialized) {
                if (state) binding.bottomNavBar.visibility =
                    View.GONE else binding.bottomNavBar.visibility = View.VISIBLE
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
                            navController?.apply {
                                popBackStack(R.id.navigation_search, false)
                            }
                        }

                        R.id.navigation_mypage -> {
                            navController?.apply {
                                popBackStack(R.id.navigation_mypage, false)
                            }
                        }

                        R.id.navigation_photo -> {

                        }

                        R.id.navigation_home -> {
                            navController?.apply {
                                popBackStack(R.id.navigation_home, false)
                            }
                        }
                    }
                }
                true
            }
        }
    }

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




}
