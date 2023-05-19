package com.lf.fashion.ui.addPost

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract

class TakePictureContract : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        // 카메라 인텐트를 생성하여 반환
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        // 액티비티 결과를 처리하고 이미지의 Uri를 반환
        if (resultCode == Activity.RESULT_OK && intent != null) {
            return intent.data
        }
        return null
    }
}