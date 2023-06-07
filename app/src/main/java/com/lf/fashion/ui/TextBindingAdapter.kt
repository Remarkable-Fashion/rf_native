package com.lf.fashion.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lf.fashion.R

import java.text.DecimalFormat
@BindingAdapter("priceFormat")
fun applyMonthsSales(view:TextView,price : Int){
    val decimalFormat = DecimalFormat("#,###")
    val formatted = view.context.getString(R.string.price_format,decimalFormat.format(price))
    view.text = formatted
}


