package com.lf.fashion.ui

import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.cancelBtnBackStack(view : ImageView){
    view.setOnClickListener {
        findNavController().popBackStack()
    }
}

fun Fragment.setOnClickListenerByViewList(viewList : List<View>,listener: View.OnClickListener){
    for( i in viewList.indices){
        viewList[i].setOnClickListener(listener)
    }
}