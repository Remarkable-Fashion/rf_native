package com.lf.fashion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
//TODO : splash 이미지
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavBar)
       // bottomNavigationView.itemIconTintList = null
        val navController = supportFragmentManager.findFragmentById(R.id.mainContainer)?.findNavController()
        navController?.let{
            bottomNavigationView.setupWithNavController(it)
        }

    }
}