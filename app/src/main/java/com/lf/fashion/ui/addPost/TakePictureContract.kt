package com.lf.fashion.ui.addPost

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.lf.fashion.TAG
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.coroutines.coroutineContext

class TakePictureContract(private val context : Context) : ActivityResultContract<Unit, Uri?>() {
    override fun createIntent(context: Context, input: Unit): Intent {
        // 카메라 인텐트를 생성하여 반환
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }
/*https://yong0810.tistory.com/35*/
    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        // 액티비티 결과를 처리하고 이미지의 Uri를 반환
        Log.d(TAG, "TakePictureContract - parseResult: ");

        if (resultCode == Activity.RESULT_OK && intent != null) {
            Log.d(TAG, "TakePictureContract - parseResult: ${intent.extras?.get("data")}");
            val bitmap = intent.extras?.get("data")
            getImageUri(context,bitmap)
            return intent.data
        }
        return null
    }
    fun getImageUri(inContext: Context?, inImage: Bitmap?): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext?.contentResolver, inImage, "Title" + " - " + Calendar.getInstance().time, null)
        return Uri.parse(path)
    }
}