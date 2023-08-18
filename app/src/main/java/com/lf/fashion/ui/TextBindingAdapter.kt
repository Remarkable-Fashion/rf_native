package com.lf.fashion.ui

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.lf.fashion.R
import com.lf.fashion.data.response.Cloth
import com.lf.fashion.data.response.Profile

import java.text.DecimalFormat

@BindingAdapter("priceFormat")
fun applyMonthsSales(view: TextView, price: Int) {
    val decimalFormat = DecimalFormat("#,###")
    val formatted = view.context.getString(R.string.price_format, decimalFormat.format(price))
    view.text = formatted
}

@BindingAdapter("bodyProfile")
fun bodyInfo(textView: TextView, profile: Profile?) {
    profile?.let {
        val sexKo = if (it.sex.equals("Male")) "남" else "여"
        var text = sexKo
        if (it.height != null) {
            text += " |  ${it.height}cm"
        }
        if (it.weight != null) {
            text += " |  ${it.weight}kg"
        }
        textView.text = text
    }
}

@BindingAdapter("userHeight")
fun height(textView: TextView, height: Int?) {
    height?.let {
        textView.text = textView.context.getString(R.string.height, it)
    }
}

@BindingAdapter("userWeight")
fun weight(textView: TextView, weight: Int?) {
    weight?.let {
        textView.text = textView.context.getString(R.string.weight, it)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("clothDetail")
fun clothDetail(textView: TextView, cloth: Cloth) {
    val color = if (cloth.color.isNullOrEmpty()) "" else cloth.color + " | "
    val size = if (cloth.size.isNullOrEmpty()) "" else cloth.size + " | "
    val decimalFormat = DecimalFormat("#,###")
    val price = textView.context.getString(R.string.price_format, decimalFormat.format(cloth.price))
    textView.text = color + size + price
}