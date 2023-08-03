package com.lf.fashion.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.lf.fashion.R
import com.lf.fashion.TAG
import com.lf.fashion.data.network.Resource
import com.lf.fashion.data.response.ChipInfo
import com.lf.fashion.data.response.MyInfo
import java.io.File
import kotlin.math.roundToInt

fun Fragment.cancelBtnBackStack(view: ImageView) {
    view.setOnClickListener {
        findNavController().popBackStack()
    }
}

@SuppressLint("InflateParams")
fun Fragment.childChip(chipList: List<ChipInfo>, chipGroup: ChipGroup, style: String) {
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
              content += " $it"
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
            if (alreadyHome != true) {
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

                val count = if (it.length > 50) 50 else it.length
                counterTextView.text = "$count/$maxLength"
            }
        }
    })
}

fun addTextChangeListener(editTexts: List<EditText>,myInfo: MyInfo, changeListener: (changed: Boolean) -> Unit) {
    for (editText in editTexts) {
        editText.addTextChangedListener(object : TextWatcher {
            private var beforeText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                beforeText = s?.toString() ?: ""
                Log.d(TAG, " - beforeTextChanged: $beforeText");
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

@SuppressLint("SetTextI18n")
fun addUnitTextListener(editText: EditText, height: Boolean) {
    val endText = if (height) "cm" else "kg"
    editText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) {
            val text = editText.text.toString()
            if (!text.endsWith(endText)) {
                editText.setText("$text $endText")
            }
            if(editText.text.toString()==" $endText"){
                val replace = editText.text.toString().replace(" $endText", "")
                editText.setText(replace)
            }
        }else{
            val replace = editText.text.toString().replace(" $endText", "")
            editText.setText(replace)
        }
    }
    editText.addTextLengthLimit(endText)
}

fun EditText.addTextLengthLimit(endText:String) {
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

fun Fragment.uriToFile(context : Context, uri : Uri) : File?{
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
    cursor?.use {
        it.moveToFirst()
        val columnIndex = it.getColumnIndex(filePathColumn[0])
        val filePath = it.getString(columnIndex)
        return File(filePath)
    }
    return null
}


fun Fragment.createDynamicLink(activity: Activity) {
    // 파라미터로 전달할 데이터를 정의합니다.
    val customParameter = "value123"

    // 파이어베이스 다이나믹 링크 빌더를 생성합니다.
    val dynamicLinkBuilder = FirebaseDynamicLinks.getInstance().createDynamicLink()
        .setDomainUriPrefix("https://example.page.link") // 도메인 URI 프리픽스를 설정합니다.
        .setLink(Uri.parse("https://example.page.link/?customParam=$customParameter")) // 링크에 파라미터를 추가합니다.
        .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build()) // Android 링크 설정
        .setIosParameters(
            DynamicLink.IosParameters.Builder("your_ios_bundle_id").build()
        ) // iOS 링크 설정
        .setSocialMetaTagParameters(
            DynamicLink.SocialMetaTagParameters.Builder().build()
        ) // 소셜 메타 태그 설정

    // 단축 다이나믹 링크를 생성하고 처리합니다.
    dynamicLinkBuilder.buildShortDynamicLink()
        .addOnSuccessListener { shortDynamicLink ->
            // 단축 링크를 얻은 후 원하는 처리를 수행합니다.
            val shortLink = shortDynamicLink.shortLink
            shortLink?.let {
                //  shareDynamicLink(it) // 링크를 공유하는 메서드 호출
            }
        }
        .addOnFailureListener(activity) { e ->
            // 링크 생성에 실패한 경우 에러 처리를 수행합니다.
            // e.message 등을 사용하여 에러 메시지를 확인할 수 있습니다.
            Log.e(TAG, "createDynamicLink: link 생성 실패 $e ")
        }

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