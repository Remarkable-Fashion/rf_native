package com.lf.fashion.ui.home.frag

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import com.lf.fashion.TAG
import com.lf.fashion.databinding.ItemGenderDialogBinding


class GenderSelectionDialog : DialogFragment() , View.OnClickListener {
    private lateinit var binding : ItemGenderDialogBinding
  /*  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =  super.onCreateDialog(savedInstanceState)
        dialog.window?.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        return dialog
    }*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemGenderDialogBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allGenderBtn.setOnClickListener(this)
        binding.genderManBtn.setOnClickListener(this)
        binding.genderWomanBtn.setOnClickListener(this)
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }
    override fun onClick(view: View?) {
        when(view){
            binding.allGenderBtn ->{
                binding.allGenderBtn.isSelected =!binding.allGenderBtn.isSelected
                this@GenderSelectionDialog.dismiss()
            }
            binding.genderManBtn ->{
                binding.genderManBtn.isSelected =!binding.allGenderBtn.isSelected
                this@GenderSelectionDialog.dismiss()
            }
            binding.genderWomanBtn ->{
                binding.genderWomanBtn.isSelected =!binding.genderWomanBtn.isSelected
                this@GenderSelectionDialog.dismiss()
            }
        }
    }

}