package com.lf.fashion.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
        val chip = when (style) {
            "default" -> {
                LayoutInflater.from(context).inflate(R.layout.chip_item, null) as Chip
            }
            "purple" -> {
                LayoutInflater.from(context).inflate(R.layout.chip_purple_item, null) as Chip
            }
            else -> {
                LayoutInflater.from(context).inflate(R.layout.chip_grey_item, null) as Chip

            }

        }
        var content = chipList[j].text

        chipList[j].emoji?.let {
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


//키보드 숨기기
fun Fragment.hideKeyboard() {
    val inputManager =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(
        requireActivity().currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}

fun Fragment.showPermissionDialog(
    requestPermissionLauncher: ActivityResultLauncher<Array<String>>,
    permissions: Array<String>
) {
    AlertDialog.Builder(requireContext()).apply {
        setMessage("이미지를 가져오기 위해서, 외부 저장소 읽기 권한이 필요합니다.")
        setNegativeButton("취소", null)
        setPositiveButton("동의") { _, _ -> requestPermissionLauncher.launch(permissions) }
    }.show()
}