package com.lf.fashion.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lf.fashion.R
import com.lf.fashion.TAG
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

fun Fragment.showRequireLoginDialog(alreadyHome: Boolean? = null) {
    val loginDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        .setMessage("로그인 후 이용가능합니다.")
        .setPositiveButton("로그인하러 가기") { _, _ ->
            //그냥 navigate to 로 보낼 경우 바텀 메뉴 이동에 오류가 생길 때가 있어서 bottomNavigationView 를 통해 이동, checked 조정
            val bottomNavigationView =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
            val loginMenuItem = bottomNavigationView.menu.findItem(R.id.navigation_mypage)
            loginMenuItem.isChecked = true
            bottomNavigationView.selectedItemId = R.id.navigation_mypage
        }
        .setNegativeButton("닫기") { _, _ ->
            if(alreadyHome != true){
                findNavController().navigateUp()
            }
        }

    loginDialog.show()

}
fun addTextLengthCounter(editText: EditText, counterTextView: TextView, maxLength: Int) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            s?.let {
                if (it.length > maxLength) {
                    val truncatedText = it.subSequence(0, maxLength)
                    editText.setText(truncatedText)
                    editText.setSelection(truncatedText.length) // Move cursor to the end
                }

                val count = if(it.length>50) 50 else it.length
                counterTextView.text = "$count/$maxLength"
            }
        }
    })
}
fun addTextChangeListener(editTexts: List<EditText>, changeListener: (changed : Boolean) -> Unit) {
    for (editText in editTexts) {
        editText.addTextChangedListener(object : TextWatcher {
            private var beforeText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeText = s?.toString() ?: ""
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val afterText = s.toString()
                if (beforeText != afterText) {
                    changeListener(true)
                }
            }
        })
    }
}
fun addUnitTextListener(editText: EditText, height:Boolean) {
    if (height) {
        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = editText.text.toString()
                if (!text.endsWith("cm")) {
                    editText.setText("$text cm")
                }
            }
        }
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    if (it.length > 3) {
                        val truncatedText = it.subSequence(0, 3)
                        editText.setText(truncatedText)
                        editText.setSelection(truncatedText.length) // Move cursor to the end
                    }
                }
            }

        })
    }else{
        editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = editText.text.toString()
                if (!text.endsWith("kg")) {
                    editText.setText("$text kg")
                }
            }
        }
        editText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                s?.let{
                if (it.length > 3) {
                    val truncatedText = it.subSequence(0, 3)
                    editText.setText(truncatedText)
                    editText.setSelection(truncatedText.length) // Move cursor to the end
                }}
            }

        })
    }
/*    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (height && !s.isNullOrEmpty() && !s.toString().endsWith("cm")) {
                editText.removeTextChangedListener(this)
                val text = s.toString() + "cm"
                editText.setText(text)
                editText.setSelection(text.length)
                editText.addTextChangedListener(this)
            }else if(!height && !s.isNullOrEmpty() && !s.toString().endsWith("kg")){
                editText.removeTextChangedListener(this)
                val text = s.toString() + "kg"
                editText.setText(text)
                editText.setSelection(text.length)
                editText.addTextChangedListener(this)
            }
        }
    })*/
}

