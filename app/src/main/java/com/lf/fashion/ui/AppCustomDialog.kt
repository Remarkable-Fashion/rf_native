package com.lf.fashion.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lf.fashion.R
import com.lf.fashion.databinding.MyCustomDialogBinding

class AppCustomDialog(
    private val msg: String,
    private val positiveBtnText: String? = null,
    private val negativeBtnText: String? = null,
    private val onClickNegative: (() -> Unit)? = {},
    private val onClickPositive: (() -> Unit)? = {}
) : DialogFragment(R.layout.my_custom_dialog) {
    private lateinit var binding: MyCustomDialogBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MyCustomDialogBinding.bind(view)
        binding.dialogMsg.text = msg
        binding.negativeBtn.text = negativeBtnText ?: "닫기"
        binding.positiveBtn.text = positiveBtnText ?: "확인"

        binding.negativeBtn.setOnClickListener {
            onClickNegative?.let {onclick ->
                    onclick() }
            this.dismiss()
        }
        binding.positiveBtn.setOnClickListener {
            onClickPositive?.let { onclick ->onclick() }
            this.dismiss()
        }


    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        dialog?.let{
            it.window!!.setLayout(
                resources.getDimensionPixelSize(R.dimen.alert_width),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            it.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 바깥 영역 터치 시 취소 리스너 설정
            it.setOnCancelListener {
                onClickNegative?.let { onclick -> onclick() }
            }
        }
    }
}