package com.lf.fashion.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lf.fashion.R
import com.lf.fashion.data.response.MyInfo

import java.text.DecimalFormat
@BindingAdapter("priceFormat")
fun applyMonthsSales(view:TextView,price : Int){
    val decimalFormat = DecimalFormat("#,###")
    val formatted = view.context.getString(R.string.price_format,decimalFormat.format(price))
    view.text = formatted
}

@BindingAdapter("bodyProfile")
fun bodyInfo(textView: TextView,userInfo : MyInfo){
    val sexKo = if (userInfo.profile.sex.equals("Male")) "남" else "여"
    textView.text = textView.context.getString(R.string.user_body_profile,sexKo,userInfo.profile.height,userInfo.profile.weight)
}

@BindingAdapter("userHeight")
fun height(textView: TextView,height:Int? ){
    height?.let{
        textView.text = textView.context.getString(R.string.height,it)
    }
}

@BindingAdapter("userWeight")
fun weight(textView: TextView,weight:Int? ){
    weight?.let{
        textView.text = textView.context.getString(R.string.weight,it)
    }
}