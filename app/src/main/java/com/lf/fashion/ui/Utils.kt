package com.lf.fashion.ui

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
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.model.ChipInfo
import com.lf.fashion.ui.addPost.UploadPostViewModel
import com.lf.fashion.ui.home.frag.FilterViewModel
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import kotlin.math.roundToInt

fun Fragment.cancelBtnBackStack(view: ImageView) {
    view.setOnClickListener {
        findNavController().popBackStack()
    }
}

@SuppressLint("InflateParams")
fun Fragment.childChip(
    chipList: List<ChipInfo>, chipGroup: ChipGroup, style: String,
    uploadPostViewModel: UploadPostViewModel? = null,
    filterViewModel: FilterViewModel? = null,
    chipOnclick: ((Int, String, Boolean) -> Unit)? = null
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
                chipOnclick(chipList[j].id, chipList[j].text, isChecked)
            }
        }

        //다른 Fragment 갔다가 돌아왔을 때 chip 을 새로 생성하는데,
        // 이때 text 값이 같으면 다시 checked를 주기 위한 작업
        //val viewModel = uploadPostViewModel ?: filterViewModel
        uploadPostViewModel?.let { it ->
            if (it.tposTexts.isNotEmpty()) {
                val text = chipList[j].text
                if (it.tposTexts.contains(text) ||
                    it.seasonsTexts.contains(text) ||
                    it.stylesTexts.contains(text)
                ) {
                    chip.isChecked = true
                }
            }
        }
        filterViewModel?.let { it ->
            if (it.tposTexts.isNotEmpty()) {
                val text = chipList[j].text
                if (it.tposTexts.contains(text) ||
                    it.seasonsTexts.contains(text) ||
                    it.stylesTexts.contains(text)
                ) {
                    chip.isChecked = true
                }
            }
        }
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
    AppCustomDialog("이미지를 가져오기 위해서, 외부 저장소 읽기 권한이 필요합니다.", "동의") {
        requestPermissionLauncher.launch(permissions)
    }.show(parentFragmentManager, "image_permission_alert")
    /*   AlertDialog.Builder(requireContext()).apply {
           setMessage("이미지를 가져오기 위해서, 외부 저장소 읽기 권한이 필요합니다.")
           setNegativeButton("취소", null)
           setPositiveButton("동의") { _, _ -> requestPermissionLauncher.launch(permissions) }
       }.show()*/
}

fun Fragment.showRequireLoginDialog(alreadyHome: Boolean? = null, presentFragId: Int) {

    AppCustomDialog(
        "로그인 후 이용가능합니다.",
        "로그인하러 가기",
        "닫기",
        onClickNegative = {
            if (alreadyHome != true) {
                findNavController().navigateUp()
            }
        }
    ) {
        //그냥 navigate to 로 보낼 경우 바텀 메뉴 이동에 오류가 생길 때가 있어서 bottomNavigationView 를 통해 이동, checked 조정
        navigateToMyPage(presentFragId)
    }.show(parentFragmentManager, "login_alert_dialog")
}

fun Fragment.navigateToMyPage(presentFragId: Int) {
    val bottomNavigationView =
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavBar)
    val loginMenuItem = bottomNavigationView.menu.findItem(R.id.navigation_mypage)
    loginMenuItem.isChecked = true
    bottomNavigationView.selectedItemId = R.id.navigation_mypage
    findNavController().popBackStack(presentFragId, true)
    findNavController().navigate(R.id.action_global_to_myPageFragment)
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
fun addUnitTextListener(editText: EditText, height: Boolean) {
    val endText = if (height) "cm" else "kg"
    editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            val text = editText.text.toString()
            if (!text.endsWith(endText)) {
                editText.setText("$text $endText")
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
    retry: (() -> Unit)? = null
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

            val error = failure.errorBody?.string().toString()
            Log.e(TAG, "handleApiError: $error")
            //requireView().snackbar(error)
        }
    }
}


// 파일 확장자로부터 MIME 타입을 추론하는 함수
fun getMimeType(file: File): String {
    Log.e(TAG, "Util getMimeType: ${file.absoluteFile}")
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

