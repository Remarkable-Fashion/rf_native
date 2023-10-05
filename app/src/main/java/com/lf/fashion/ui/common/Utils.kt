package com.lf.fashion.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.JsonParser
import com.lf.fashion.MainActivity
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.ChipInfo
import com.lf.fashion.ui.addPost.UploadPostViewModel
import com.lf.fashion.ui.globalFrag.editPost.EditPostViewModel
import com.lf.fashion.ui.home.frag.FilterViewModel
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import kotlin.math.roundToInt

const val NEED_TO_REFRESH ="need_to_refresh"
fun Fragment.cancelBtnBackStack(view: ImageView,needRefresh : Boolean?=null) {
    view.setOnClickListener {
        if(needRefresh==true){
            setFragmentResult(NEED_TO_REFRESH, bundleOf("refresh" to true))
        }
        findNavController().popBackStack()
    }
}

@SuppressLint("InflateParams")
fun Fragment.childChip(
    chipList: List<ChipInfo>, chipGroup: ChipGroup, style: String,
    uploadPostViewModel: UploadPostViewModel? = null,
    filterViewModel: FilterViewModel? = null,
    editPostViewModel : EditPostViewModel?=null,
    chipOnclick: ((ChipInfo, Boolean) -> Unit)? = null
) {
    for (j in chipList.indices) {
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
            content += " $it"
        }

        chip.text = content
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (chipOnclick != null) {
                /*.id, chipList[j].text*/
                chipOnclick(chipList[j], isChecked)
            }
        }

        //다른 Fragment 갔다가 돌아왔을 때 chip 을 새로 생성하는데,
        // 이때 text 값이 같으면 다시 checked를 주기 위한 작업
        //val viewModel = uploadPostViewModel ?: filterViewModel
        // id 값을 구별자로 주변 tpos seasons styles 의 아이디가 서로 겹치기때문에 불가.
        val text = chipList[j].text
        uploadPostViewModel?.let {
            extracted(it.selectedTpos,it.selectedSeasons,it.selectedStyles, text, chip)
        }
        filterViewModel?.let {
            extracted(it.selectedTpos,it.selectedSeasons,it.selectedStyles, text, chip)
        }
        editPostViewModel?.let{
            extracted(it.selectedTpos,it.selectedSeasons,it.selectedStyles, text, chip)
        }
        chipGroup.addView(chip)
    }
}

private fun extracted(
    tpos: MutableList<ChipInfo>,
    seasons: MutableList<ChipInfo>,
    styles: MutableList<ChipInfo>,
    text: String,
    chip: Chip
) {
    val tposTexts = tpos.map { it.text }
    val seasonsTexts = seasons.map { it.text }
    val stylesTexts = styles.map { it.text }
    if (tposTexts.contains(text) ||
        seasonsTexts.contains(text) ||
        stylesTexts.contains(text)
    ) {
        chip.isChecked = true
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
    AppCustomDialog("이미지를 가져오기 위해서, 외부 저장소 읽기 권한이 필요합니다.", null,"동의") {
        requestPermissionLauncher.launch(permissions)
    }.show(parentFragmentManager, "image_permission_alert")

}

fun Fragment.showRequireLoginDialog(alreadyHome: Boolean? = null) {

    AppCustomDialog(
        "로그인 후 이용가능합니다.",
        null,"로그인하러 가기",
        "닫기",
        onClickNegative = {
            if (alreadyHome != true) {
                findNavController().navigate(R.id.navigation_home)
            }
        }
    ) {
        findNavController().navigate(R.id.navigation_mypage)
    }.show(parentFragmentManager, "login_alert_dialog")
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

                val count = if (it.length > 50) 50 else it.length
                counterTextView.text = "$count/$maxLength"
            }
        }
    })
}

fun addTextChangeListener(
    editTexts: List<EditText>,
    originalValues: Map<EditText, String?>,
    changeListener: (changed: Boolean) -> Unit
) {
    val editTextChangeListeners = mutableMapOf<EditText, TextWatcher>()

    for (editText in editTexts) {
        val originalValue = originalValues[editText]
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val currentValue = s?.toString() ?: ""
                val changed = currentValue != originalValue
                changeListener(changed)
            }
        }
        editText.addTextChangedListener(textWatcher)
        editTextChangeListeners[editText] = textWatcher
    }
}

@SuppressLint("SetTextI18n")
fun addUnitTextListener(editText: EditText, height: Boolean ,  valueListener: ((value: String) -> Unit)?=null) {
    val endText = if (height) "cm" else "kg"
    editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            val text = editText.text.toString()
            if (!text.endsWith(endText)) {
                editText.setText("$text $endText")
                valueListener?.let{//viewModel 에 height weight 값을 저장할 수 있도록 뱉어주기
                    valueListener(text)
                }
            }
            if (editText.text.toString() == " $endText") {
                val replace = editText.text.toString().replace(" $endText", "")
                editText.setText(replace)
            }
        } else {
            val replace = editText.text.toString().replace(" $endText", "")
            editText.setText(replace)
        }
    }
    editText.addTextLengthLimit(endText)
}

fun EditText.addTextLengthLimit(endText: String) {
    this@addTextLengthLimit.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            s?.let {
                val number = it.toString().replace(endText, "")
                if (number.length > 3) {
                    val truncatedText = "${number.subSequence(0, 3)} $endText"
                    this@addTextLengthLimit.removeTextChangedListener(this)
                    this@addTextLengthLimit.setText(truncatedText)
                    this@addTextLengthLimit.setSelection(truncatedText.length) // Move cursor to the end
                    this@addTextLengthLimit.addTextChangedListener(this)
                }
            }
        }

    })
}

fun itemViewRatioSetting(
    context: Context,
    itemView: View,
    spanCount: Int?,
    reduceViewWidth: Boolean? = null
) {
    val aspectRatio = 4f / 3f // 3:4 비율

    // 현재 spanCount에 따라 너비와 높이를 조정
    val layoutParams = itemView.layoutParams as ViewGroup.MarginLayoutParams
    var screenWidth = context.resources.displayMetrics.widthPixels
    if (reduceViewWidth == true) {
        screenWidth -= (0.1 * screenWidth).toInt()
    }
    val itemWidth = screenWidth / (spanCount ?: 2)

    layoutParams.width = itemWidth
    layoutParams.height = (itemWidth * aspectRatio).toInt()
}

fun Fragment.absolutelyPath(path: Uri?, context: Context): String? {
    val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
    val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
    val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    c?.moveToFirst()

    return c?.getString(index!!)
}


fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: ((String) -> Unit)? = null
) {
    when {
        failure.isNetworkError -> {
            /*  try {
                  val findViewById = requireActivity().findViewById<View>(R.id.abb_bar_layout)
                  findViewById.snackbar(
                      R.string.network_message,
                      retry
                  )
              }catch (e:NullPointerException){
                  Toast.makeText(context, R.string.network_message, Toast.LENGTH_SHORT).show()
              }*/
        }

        failure.errorCode == 401 -> {
            //  Toast.makeText(context, R.string.login_over, Toast.LENGTH_SHORT).show()

            //logout()
        }

        else -> {
            retry?.let { callback->
                val error = failure.errorBody?.string().toString()
                try {

                    val json = JsonParser.parseString(error).asJsonObject
                    val msg = json.get("msg").asString
                    callback(msg)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            //requireView().snackbar(error)
        }
    }
}


// 파일 확장자로부터 MIME 타입을 추론하는 함수
fun getMimeType(file: File): String {
    val split = file.absoluteFile.toString().split(".")
    val mime = split[1]
    //val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
    //return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    Log.e(TAG, "getMimeType: image/${mime}")
    return "image/${mime}"
}

fun getAssetsTextString(mContext: Context, fileName: String): String {
    val termsString = StringBuilder()
    val reader: BufferedReader

    try {
        reader = BufferedReader(
            InputStreamReader(mContext.resources.assets.open("$fileName.txt"))
        )

        var str: String?
        while (reader.readLine().also { str = it } != null) {
            termsString.append(str)
            termsString.append('\n') //줄 변경
        }
        reader.close()
        return termsString.toString()

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return "fail"
}
fun Fragment.mainBottomMenuListener(setting : Boolean){
    if(setting){
        MainActivity.bottomNaviReselectedListener(findNavController())
        MainActivity.bottomNaviSetItemSelectedListener(findNavController())
    }else{
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
        bottomNavigationView.setOnItemSelectedListener(null)
        bottomNavigationView.setOnItemReselectedListener(null)
    }
}
