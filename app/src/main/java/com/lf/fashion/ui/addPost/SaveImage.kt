package com.lf.fashion.ui.addPost

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream

class SaveImage(private val context: Context)  {

     fun saveImageToGallery(bitmap: Bitmap): Uri? {
        val filename = "${System.currentTimeMillis()}.jpg"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 이상
            saveImageToGalleryAndroid10Plus(bitmap, filename)
        } else {
            // Android 10 미만
            saveImageToGalleryAndroid9Minus(bitmap, filename)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageToGalleryAndroid10Plus(bitmap: Bitmap, filename: String) :Uri?{
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val resolver = context.contentResolver

       return try {
            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val imageUri = resolver.insert(collection, contentValues)

            if (imageUri != null) {
                val outputStream = resolver.openOutputStream(imageUri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream?.close()

                // 저장 완료 메시지 표시
                Toast.makeText(context, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()

                imageUri
            }else{
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "이미지 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun saveImageToGalleryAndroid9Minus(bitmap: Bitmap, filename: String):Uri? {
        val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDirectory, filename)

       return try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // 저장 완료 메시지 표시
            Toast.makeText(context, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()

           // 갤러리에 이미지 추가
           val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
           val contentUri = Uri.fromFile(imageFile)
           mediaScanIntent.data = contentUri
           context.sendBroadcast(mediaScanIntent)

           contentUri
        } catch (e: Exception) {
            e.printStackTrace()
           Toast.makeText(context, "이미지 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
           null
        }

    }
}