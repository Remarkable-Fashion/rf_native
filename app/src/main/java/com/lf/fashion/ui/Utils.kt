package com.lf.fashion.ui

import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.cancelBtnBackStack(view : ImageView){
    view.setOnClickListener {
        findNavController().popBackStack()
    }
}