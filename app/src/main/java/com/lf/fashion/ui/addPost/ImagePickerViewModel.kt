package com.lf.fashion.ui.addPost

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils.indexOf
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lf.fashion.TAG
import com.lf.fashion.data.response.ImageItem
import java.io.File

private const val INDEX_MEDIA_ID = MediaStore.MediaColumns._ID
private const val INDEX_MEDIA_URI = MediaStore.MediaColumns.DATA
private const val INDEX_ALBUM_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
private const val INDEX_DATE_ADDED = MediaStore.MediaColumns.DATE_ADDED

/**
 * 최종 선택된 이미지를 참조할 때는 checkedItemList 를 참조하는 것이 아니라 getCheckedImageUriList 로 참조할것 !
 *
 */
class ImagePickerViewModel(context: Context) : ViewModel() {
    val imageItemList = MutableLiveData<MutableList<ImageItem>>(mutableListOf())
    val checkedItemList = MutableLiveData<MutableList<ImageItem>>()

    init {
        fetchImageItemList(context)
    }

    private fun fetchImageItemList(context: Context) {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )
        val selection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.SIZE + " > 0"
            else null
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val cursor = context.contentResolver.query(uri, projection, selection, null, sortOrder)

        cursor?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                 imageItemList.value!!.add(
                     ImageItem(contentUri, false, ""))
            }
        }
    }

    fun getCheckedImageUriList(): MutableList<String> {
        val checkedImageUriList = mutableListOf<String>()
        imageItemList.value?.let {
            for (imageItem in imageItemList.value!!) {
                if (imageItem.isChecked) checkedImageUriList.add(imageItem.uri.toString())
            }
        }
        return checkedImageUriList
    }

    fun cancelCheck(uncheckedItem: ImageItem) {
        //이미지 리스트에서 checked false 로 변경
        val tempList = imageItemList.value
        tempList?.let {
            for (imageItem in it) {
                if (imageItem.uri == uncheckedItem.uri) {
                    imageItem.isChecked = false
                    imageItem.checkCount = ""
                }
            }
            imageItemList.value = it
        }

        //체크리스트에서 삭제
        val checkTemp = checkedItemList.value?.toMutableList()
        checkTemp?.removeIf { checked -> checked.uri == uncheckedItem.uri }
        checkTemp?.let {
            checkedItemList.value = it
        }
        updateCheckedCountNum()
    }

    // 이미지를 기존 리스트에 추가하는 메서드
    fun addImageToImageList(newItem: ImageItem) {
        val currentList = imageItemList.value
        currentList?.let {
            it.add(newItem)
            imageItemList.value = it
        }
    }

    fun addCheckedItem(item: ImageItem) {
        if (checkedItemList.value == null) {
            val temporal = mutableListOf(item)
            checkedItemList.value = temporal
        } else {
            val temporal = checkedItemList.value
            temporal!!.add(item)
            checkedItemList.value = temporal!!
        }
        updateCheckedCountNum()
    }

    // 이미지를 선택했을때 몇번째로 선택된 이미지인지 표시하기 위한 카운터 update 메소드
    private fun updateCheckedCountNum() {
        val temporal = imageItemList.value
        val value = checkedItemList.value
        temporal?.let { tem ->
            tem.map {
                val checkCount = value?.indexOf(it)?:-1
                val str = if(checkCount >= 0){ (checkCount + 1).toString() } else ""
                it.checkCount = str
            }
            imageItemList.value = tem
        }
    }
}

class ImagePickerViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImagePickerViewModel::class.java)) {
            return ImagePickerViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
