package com.lf.fashion.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lf.fashion.R
import com.lf.fashion.data.response.ChipContents
import kotlin.math.roundToInt

fun Fragment.cancelBtnBackStack(view: ImageView) {
    view.setOnClickListener {
        findNavController().popBackStack()
    }
}

@SuppressLint("InflateParams")
fun Fragment.childChip(chipList: List<ChipContents>, chipGroup: ChipGroup, style: String) {
    for (j in chipList.indices) {
       /* val chip =
            if (style == "default") LayoutInflater.from(context)
                .inflate(R.layout.chip_item, null) as Chip
            else LayoutInflater.from(context).inflate(R.layout.chip_purple_item, null) as Chip*/
        val chip = when(style){
            "default" ->{
                LayoutInflater.from(context).inflate(R.layout.chip_item, null) as Chip }
            "purple" ->{
                LayoutInflater.from(context).inflate(R.layout.chip_purple_item, null) as Chip }
            else -> { LayoutInflater.from(context).inflate(R.layout.chip_grey_item, null) as Chip}
        }
        var content = chipList[j].text

        chipList[j].emoji?.let{
            val emoji = it.substring(2).toInt(16)
                content += " " + String(Character.toChars(emoji))
        }

        chip.text = content
        chipGroup.addView(chip)
    }
}

fun convertDPtoPX(context: Context, dp: Int): Int {
    val density: Float = context.resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}